package com.example.tengyunapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.tengyunapiclientsdk.utils.SignUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 腾云 API 客户端
 * 开发者引入 SDK 后，只需调用此类的方法即可与咱们的网关交互
 */
public class TengyunApiClient {

    private final String accessKey;
    private final String secretKey;

    // 网关的统一入口地址 (后续咱们写网关时会用到)
    private static final String GATEWAY_HOST = "http://localhost:8090";

    public TengyunApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 核心装配厂：把需要传递给网关的参数全部塞进请求头 (Header)
     */
    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        // 1. 标识身份的公钥
        hashMap.put("accessKey", accessKey);
        // ⚠️ 绝密警告：千万不能把 secretKey 放在请求头里发出去！
        // 故意改错 AK，模拟未授权用户或者篡改行为
        //hashMap.put("accessKey", accessKey + "hacker");
        // 2. 随机数 (防重放攻击)
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        // 3. 请求体内容
        hashMap.put("body", body);
        // 4. 时间戳 (防过期请求)
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 5. 签名 (基于 body 和 私钥 动态计算)
        hashMap.put("sign", SignUtils.getSign(body, secretKey));

        return hashMap;
    }

    /**
     * 测试接口调用示例
     */
    public String invokeTestApi(String name) {
        // 将参数和加密后的 Header 一起发送给网关
        HttpResponse response = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(name))
                .body(name)
                .execute();
        System.out.println("网关返回状态码：" + response.getStatus());
        return response.body();
    }
}