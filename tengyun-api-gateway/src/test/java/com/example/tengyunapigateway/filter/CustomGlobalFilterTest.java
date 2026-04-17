package com.example.tengyunapigateway.filter;

import com.example.tengyunapiclientsdk.utils.SignUtils;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.service.InnerInterfaceInfoService;
import com.example.tengyunapicommon.service.InnerInterfaceInvokeLogService;
import com.example.tengyunapicommon.service.InnerUserInterfaceInfoService;
import com.example.tengyunapicommon.service.InnerUserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomGlobalFilterTest {

    @Test
    void shouldReturnUnauthorizedWhenAuthHeadersMissing() {
        CustomGlobalFilter filter = buildFilterWithBaseMocks();
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/name/user").build());

        filter.filter(exchange, ignored -> Mono.empty()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldBlockReplayNonceRequest() {
        InnerUserInterfaceInfoService quotaService = mock(InnerUserInterfaceInfoService.class);
        when(quotaService.hasInvokeCount(anyLong(), anyLong())).thenReturn(true);
        when(quotaService.invokeCount(anyLong(), anyLong())).thenReturn(true);

        InnerInterfaceInvokeLogService invokeLogService = mock(InnerInterfaceInvokeLogService.class);
        when(invokeLogService.addInvokeLog(any())).thenReturn(100L);
        when(invokeLogService.claimInvokeLogForBilling(eq(100L), any(), any())).thenReturn(true);

        CustomGlobalFilter filter = buildFilterWithBaseMocks();
        ReflectionTestUtils.setField(filter, "innerUserInterfaceInfoService", quotaService);
        ReflectionTestUtils.setField(filter, "innerInterfaceInvokeLogService", invokeLogService);

        long now = System.currentTimeMillis() / 1000;
        String timestamp = String.valueOf(now);
        String nonce = "12345678";
        String path = "/api/name/user";
        String method = "POST";
        String body = "{\"name\":\"tengyun\"}";
        String sign = SignUtils.getSign(path, method, body, timestamp, nonce, "sk-test");

        ServerWebExchange firstExchange = buildExchange(path, method, body, timestamp, nonce, sign);
        filter.filter(firstExchange, chain -> {
            chain.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }).block();
        assertEquals(HttpStatus.OK, firstExchange.getResponse().getStatusCode());

        ServerWebExchange secondExchange = buildExchange(path, method, body, timestamp, nonce, sign);
        filter.filter(secondExchange, chain -> {
            chain.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }).block();
        assertEquals(HttpStatus.UNAUTHORIZED, secondExchange.getResponse().getStatusCode());

        verify(quotaService, times(1)).hasInvokeCount(1L, 2L);
        verify(quotaService, times(1)).invokeCount(1L, 2L);
    }

    private CustomGlobalFilter buildFilterWithBaseMocks() {
        InnerInterfaceInfoService interfaceInfoService = mock(InnerInterfaceInfoService.class);
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(1L);
        interfaceInfo.setStatus(1);
        when(interfaceInfoService.getInterfaceInfo("/api/name/user", "POST")).thenReturn(interfaceInfo);

        InnerUserService userService = mock(InnerUserService.class);
        User user = new User();
        user.setId(2L);
        user.setAccessKey("ak-test");
        user.setSecretKey("sk-test");
        when(userService.getInvokeUser("ak-test")).thenReturn(user);

        InnerUserInterfaceInfoService quotaService = mock(InnerUserInterfaceInfoService.class);
        when(quotaService.hasInvokeCount(anyLong(), anyLong())).thenReturn(false);

        InnerInterfaceInvokeLogService invokeLogService = mock(InnerInterfaceInvokeLogService.class);

        CustomGlobalFilter filter = new CustomGlobalFilter();
        ReflectionTestUtils.setField(filter, "innerInterfaceInfoService", interfaceInfoService);
        ReflectionTestUtils.setField(filter, "innerUserService", userService);
        ReflectionTestUtils.setField(filter, "innerUserInterfaceInfoService", quotaService);
        ReflectionTestUtils.setField(filter, "innerInterfaceInvokeLogService", invokeLogService);
        return filter;
    }

    private ServerWebExchange buildExchange(
            String path,
            String method,
            String body,
            String timestamp,
            String nonce,
            String sign) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.method(method, path)
                .header("accessKey", "ak-test")
                .header("nonce", nonce)
                .header("timestamp", timestamp)
                .header("sign", sign)
                .header("body", body);
        return MockServerWebExchange.from(builder);
    }
}
