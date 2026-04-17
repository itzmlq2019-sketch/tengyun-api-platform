package com.example.tengyunapibackend.service.impl;

import com.example.tengyunapibackend.model.request.InterfaceInfoAddRequest;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapibackend.service.InterfaceAdminQueryService;
import com.example.tengyunapibackend.service.InterfaceInfoService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InterfaceInfoFacadeServiceImplTest {

    @Test
    void addInterfaceInfoShouldRequireAdminAndRecordAuditLog() {
        InterfaceInfoService interfaceInfoService = mock(InterfaceInfoService.class);
        InterfaceAdminQueryService interfaceAdminQueryService = mock(InterfaceAdminQueryService.class);
        AdminOperateLogService adminOperateLogService = mock(AdminOperateLogService.class);
        AuthContextService authContextService = mock(AuthContextService.class);
        RequestValidationService requestValidationService = mock(RequestValidationService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        User admin = new User();
        admin.setId(1L);
        when(authContextService.requireAdmin(request)).thenReturn(admin);
        doAnswer(invocation -> {
            InterfaceInfo interfaceInfo = invocation.getArgument(0);
            interfaceInfo.setId(100L);
            return true;
        }).when(interfaceInfoService).save(any(InterfaceInfo.class));

        InterfaceInfoFacadeServiceImpl facade = new InterfaceInfoFacadeServiceImpl();
        ReflectionTestUtils.setField(facade, "interfaceInfoService", interfaceInfoService);
        ReflectionTestUtils.setField(facade, "interfaceAdminQueryService", interfaceAdminQueryService);
        ReflectionTestUtils.setField(facade, "adminOperateLogService", adminOperateLogService);
        ReflectionTestUtils.setField(facade, "authContextService", authContextService);
        ReflectionTestUtils.setField(facade, "requestValidationService", requestValidationService);

        InterfaceInfoAddRequest addRequest = new InterfaceInfoAddRequest();
        addRequest.setName("test-api");
        addRequest.setUrl("/api/name/user");
        addRequest.setMethod("POST");

        long interfaceId = facade.addInterfaceInfo(addRequest, request);

        assertEquals(100L, interfaceId);
        verify(authContextService, times(1)).requireAdmin(request);
        verify(adminOperateLogService, times(1)).record(
                eq(admin),
                eq("INTERFACE_ADD"),
                eq("interface"),
                eq(100L),
                eq("name=test-api"),
                eq(request));
    }
}
