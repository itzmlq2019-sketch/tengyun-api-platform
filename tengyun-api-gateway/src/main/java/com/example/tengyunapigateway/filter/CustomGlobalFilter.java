package com.example.tengyunapigateway.filter;

import com.example.tengyunapiclientsdk.utils.SignUtils;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.service.InnerInterfaceInfoService;
import com.example.tengyunapicommon.service.InnerUserInterfaceInfoService;
import com.example.tengyunapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局安全拦截器
 * 核心职责：拦截所有请求，校验 AK、SK、时间戳和签名
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    // 🌟 核心：网关不连数据库，直接向后台打 RPC 电话要数据！
    @DubboReference
    private InnerUserService innerUserService;
    // ⚠️ 模拟数据库里的秘钥：目前先写死，后续咱们会用 Dubbo RPC 从 backend 后台数据库里动态查！
    //private static final String MOCK_ACCESS_KEY = "tengyun_admin_ak_123456";
    //private static final String MOCK_SECRET_KEY = "tengyun_admin_sk_abcdef";


    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取请求基本信息
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();
        log.info("网关收到请求，路径: {}，方法: {}", path, method);

        // 🛡️ 防线一：校验接口是否存在（这是为了拿到 interfaceInfoId）
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        if (interfaceInfo == null) {
            log.error("拦截：接口 [{}] 不存在或方法错误！", path);
            return handleNoAuth(exchange.getResponse());
        }
        Long interfaceInfoId = interfaceInfo.getId();

        // 2. 提取 SDK 加密参数
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // 🛡️ 防线二：参数完整性校验
        if (accessKey == null || nonce == null || timestamp == null || sign == null || body == null) {
            log.error("拦截：请求头参数不完整！");
            return handleNoAuth(exchange.getResponse());
        }

        // 🛡️ 防线三：通过 RPC 查询用户及 SK
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if (invokeUser == null) {
            log.error("拦截：查无此人，非法的 AccessKey！");
            return handleNoAuth(exchange.getResponse());
        }
        Long userId = invokeUser.getId();

        // 🛡️ 防线四：防重放（时间戳校验）
        long currentTime = System.currentTimeMillis() / 1000;
        if ((currentTime - Long.parseLong(timestamp)) >= 5 * 60L) {
            log.error("拦截：请求已过期！");
            return handleNoAuth(exchange.getResponse());
        }

        // 🛡️ 防线五：签名校验
        String serverSign = SignUtils.getSign(body, invokeUser.getSecretKey());
        if (!sign.equals(serverSign)) {
            log.error("拦截：签名校验失败！");
            return handleNoAuth(exchange.getResponse());
        }

        // 🟢 鉴权通过！准备执行转发并在完成后扣费
        log.info("鉴权通过，正在转发至接口服务...");

        // 🌟 核心：利用 WebFlux 的 then 方法，在响应返回后执行逻辑
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            HttpStatusCode statusCode = response.getStatusCode();

            // 💰 只有当业务接口真实返回 200 OK 时，才触发后台扣费
            if (statusCode != null && statusCode.isSameCodeAs(HttpStatus.OK)) {
                log.info("【计费系统】调用成功，触发异步扣费。用户ID: {}，接口ID: {}", userId, interfaceInfoId);
                try {
                    // 异步通知后台修改数据库
                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                } catch (Exception e) {
                    log.error("【计费系统】RPC 扣费失败！错误信息: {}", e.getMessage());
                }
            } else {
                log.warn("【计费系统】接口返回状态码为 {}，放弃扣费。", statusCode);
            }
        }));
    }
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
    @Override
    public int getOrder() {
        // -1 代表最高优先级，最先执行
        return -1;
    }
}