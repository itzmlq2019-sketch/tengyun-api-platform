package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.InterfaceInfoAddRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoInvokeRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoUpdateRequest;
import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapibackend.service.InterfaceAdminQueryService;
import com.example.tengyunapibackend.service.InterfaceInfoFacadeService;
import com.example.tengyunapibackend.service.InterfaceInfoService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapiclientsdk.client.TengyunApiClient;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
public class InterfaceInfoFacadeServiceImpl implements InterfaceInfoFacadeService {

    private static final int STATUS_OFFLINE = 0;
    private static final int STATUS_ONLINE = 1;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "name", "method", "status", "create_time", "update_time");

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceAdminQueryService interfaceAdminQueryService;

    @Resource
    private AdminOperateLogService adminOperateLogService;

    @Resource
    private AuthContextService authContextService;

    @Resource
    private RequestValidationService requestValidationService;

    @Override
    public long addInterfaceInfo(InterfaceInfoAddRequest request, HttpServletRequest requestContext) {
        User admin = authContextService.requireAdmin(requestContext);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validateInterfaceInfoRequest(request.getName(), request.getUrl(), request.getMethod());
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(request, interfaceInfo);
        interfaceInfo.setStatus(STATUS_OFFLINE);
        boolean saveResult = interfaceInfoService.save(interfaceInfo);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增接口失败");
        }
        adminOperateLogService.record(
                admin,
                "INTERFACE_ADD",
                "interface",
                interfaceInfo.getId(),
                "name=" + interfaceInfo.getName(),
                requestContext);
        return interfaceInfo.getId();
    }

    @Override
    public boolean updateInterfaceInfo(InterfaceInfoUpdateRequest request, HttpServletRequest requestContext) {
        User admin = authContextService.requireAdmin(requestContext);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getId(), "id");
        validateInterfaceInfoRequest(request.getName(), request.getUrl(), request.getMethod());
        InterfaceInfo existInterfaceInfo = interfaceInfoService.getById(request.getId());
        if (existInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(request, interfaceInfo);
        boolean updateResult = interfaceInfoService.updateById(interfaceInfo);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新接口失败");
        }
        adminOperateLogService.record(
                admin,
                "INTERFACE_UPDATE",
                "interface",
                request.getId(),
                "name=" + request.getName(),
                requestContext);
        return true;
    }

    @Override
    public boolean deleteInterfaceInfo(Long id, HttpServletRequest requestContext) {
        User admin = authContextService.requireAdmin(requestContext);
        requestValidationService.requirePositiveId(id, "id");
        boolean deleted = interfaceInfoService.deleteInterfaceInfo(id);
        if (deleted) {
            adminOperateLogService.record(
                    admin,
                    "INTERFACE_DELETE",
                    "interface",
                    id,
                    "delete interface",
                    requestContext);
        }
        return deleted;
    }

    @Override
    public InterfaceInfo getInterfaceInfo(Long id) {
        requestValidationService.requirePositiveId(id, "id");
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        return interfaceInfo;
    }

    @Override
    public List<InterfaceInfo> listInterfaceInfo(String method, Integer status, String name) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(method != null && !method.isBlank(), "method", method);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.like(name != null && !name.isBlank(), "name", name);
        queryWrapper.orderByDesc("id");
        return interfaceInfoService.list(queryWrapper);
    }

    @Override
    public Page<InterfaceInfo> pageInterfaceInfo(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(method != null && !method.isBlank(), "method", method);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.like(name != null && !name.isBlank(), "name", name);

        String normalizedSortField = sortField == null ? "id" : sortField.trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            normalizedSortField = "id";
        }
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(true, ascending, normalizedSortField);
        return interfaceInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public List<InterfaceAdminVO> listAdminInterfaceInfo(
            String method,
            Integer status,
            String name,
            HttpServletRequest request) {
        boolean includeSensitiveFields = authContextService.isAdmin(request);
        return interfaceAdminQueryService.listInterfaces(method, status, name, includeSensitiveFields);
    }

    @Override
    public Page<InterfaceAdminVO> pageAdminInterfaceInfo(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            HttpServletRequest request) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        boolean includeSensitiveFields = authContextService.isAdmin(request);
        return interfaceAdminQueryService.pageInterfaces(
                method, status, name, pageNum, pageSize, includeSensitiveFields);
    }

    @Override
    public boolean onlineInterfaceInfo(Long id, HttpServletRequest requestContext) {
        User admin = authContextService.requireAdmin(requestContext);
        boolean updated = interfaceInfoService.updateInterfaceStatus(id, STATUS_ONLINE);
        if (updated) {
            adminOperateLogService.record(
                    admin,
                    "INTERFACE_ONLINE",
                    "interface",
                    id,
                    "status=online",
                    requestContext);
        }
        return updated;
    }

    @Override
    public boolean offlineInterfaceInfo(Long id, HttpServletRequest requestContext) {
        User admin = authContextService.requireAdmin(requestContext);
        boolean updated = interfaceInfoService.updateInterfaceStatus(id, STATUS_OFFLINE);
        if (updated) {
            adminOperateLogService.record(
                    admin,
                    "INTERFACE_OFFLINE",
                    "interface",
                    id,
                    "status=offline",
                    requestContext);
        }
        return updated;
    }

    @Override
    public String invokeInterface(InterfaceInfoInvokeRequest request, HttpServletRequest requestContext) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getId(), "id");
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(request.getId());
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        if (!Integer.valueOf(STATUS_ONLINE).equals(interfaceInfo.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口未上线，无法调用");
        }
        User loginUser = authContextService.getLoginUser(requestContext);
        if (loginUser.getAccessKey() == null || loginUser.getSecretKey() == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "当前用户缺少 AK/SK");
        }
        TengyunApiClient apiClient = new TengyunApiClient(loginUser.getAccessKey(), loginUser.getSecretKey());
        try {
            return apiClient.invoke(
                    normalizePath(interfaceInfo.getUrl()),
                    interfaceInfo.getMethod(),
                    request.getUserRequestParams()
            );
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败: " + e.getMessage());
        }
    }

    private void validateInterfaceInfoRequest(String name, String url, String method) {
        if (name == null || name.isBlank() || url == null || url.isBlank() || method == null || method.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称、地址、请求方法不能为空");
        }
    }

    private String normalizePath(String url) {
        if (url == null || url.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不能为空");
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return URI.create(url).getPath();
        }
        return url;
    }
}
