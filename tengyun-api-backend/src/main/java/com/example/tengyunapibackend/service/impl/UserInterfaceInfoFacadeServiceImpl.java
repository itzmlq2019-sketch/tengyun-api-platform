package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoGrantRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoRevokeRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoStatusUpdateRequest;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapibackend.service.UserInterfaceInfoFacadeService;
import com.example.tengyunapibackend.service.UserInterfaceInfoService;
import com.example.tengyunapibackend.service.UserInterfaceQuotaRecordService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserInterfaceInfoFacadeServiceImpl implements UserInterfaceInfoFacadeService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "left_num", "total_num", "status", "create_time", "update_time");
    private static final Set<String> ALLOWED_QUOTA_RECORD_SORT_FIELDS = Set.of(
            "id", "change_num", "create_time", "update_time");

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceQuotaRecordService userInterfaceQuotaRecordService;

    @Resource
    private AdminOperateLogService adminOperateLogService;

    @Resource
    private AuthContextService authContextService;

    @Resource
    private RequestValidationService requestValidationService;

    @Override
    public boolean grantInvokeCount(UserInterfaceInfoGrantRequest request, HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getUserId(), "userId");
        requestValidationService.requirePositiveId(request.getInterfaceInfoId(), "interfaceInfoId");
        requestValidationService.requirePositiveNumber(request.getGrantNum(), "grantNum");
        boolean result = userInterfaceInfoService.grantInvokeCount(request);
        if (result) {
            adminOperateLogService.record(
                    admin,
                    "AUTH_GRANT_QUOTA",
                    "user_interface_info",
                    request.getInterfaceInfoId(),
                    "userId=" + request.getUserId() + ",grantNum=" + request.getGrantNum(),
                    httpServletRequest);
        }
        return result;
    }

    @Override
    public boolean revokeInvokeCount(UserInterfaceInfoRevokeRequest request, HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getUserId(), "userId");
        requestValidationService.requirePositiveId(request.getInterfaceInfoId(), "interfaceInfoId");
        requestValidationService.requirePositiveNumber(request.getRevokeNum(), "revokeNum");
        boolean result = userInterfaceInfoService.revokeInvokeCount(request);
        if (result) {
            adminOperateLogService.record(
                    admin,
                    "AUTH_REVOKE_QUOTA",
                    "user_interface_info",
                    request.getInterfaceInfoId(),
                    "userId=" + request.getUserId() + ",revokeNum=" + request.getRevokeNum(),
                    httpServletRequest);
        }
        return result;
    }

    @Override
    public boolean updateAuthorizationStatus(
            UserInterfaceInfoStatusUpdateRequest request,
            HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getUserId(), "userId");
        requestValidationService.requirePositiveId(request.getInterfaceInfoId(), "interfaceInfoId");
        if (request.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userInterfaceInfoService.updateAuthorizationStatus(request);
        if (result) {
            adminOperateLogService.record(
                    admin,
                    "AUTH_UPDATE_STATUS",
                    "user_interface_info",
                    request.getInterfaceInfoId(),
                    "userId=" + request.getUserId() + ",status=" + request.getStatus(),
                    httpServletRequest);
        }
        return result;
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfo(Long userId, Long interfaceInfoId, HttpServletRequest request) {
        requestValidationService.requirePositiveId(userId, "userId");
        requestValidationService.requirePositiveId(interfaceInfoId, "interfaceInfoId");
        authContextService.requireSelfOrAdmin(userId, request);

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("interface_info_id", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户接口额度记录不存在");
        }
        return userInterfaceInfo;
    }

    @Override
    public List<UserInterfaceInfo> listUserInterfaceInfo(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            HttpServletRequest request) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = buildQueryWrapper(userId, interfaceInfoId, status, request);
        queryWrapper.orderByDesc("id");
        return userInterfaceInfoService.list(queryWrapper);
    }

    @Override
    public Page<UserInterfaceInfo> pageUserInterfaceInfo(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        QueryWrapper<UserInterfaceInfo> queryWrapper = buildQueryWrapper(userId, interfaceInfoId, status, request);

        String normalizedSortField = sortField == null ? "id" : sortField.trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            normalizedSortField = "id";
        }
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(true, ascending, normalizedSortField);
        return userInterfaceInfoService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public AuthorizationSummaryVO getAuthorizationSummary(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            HttpServletRequest request) {
        authContextService.requireAdmin(request);
        return userInterfaceInfoService.getAuthorizationSummary(userId, interfaceInfoId, status);
    }

    @Override
    public List<UserInterfaceQuotaRecord> listQuotaRecords(
            Long userId,
            Long interfaceInfoId,
            String operateType,
            HttpServletRequest request) {
        QueryWrapper<UserInterfaceQuotaRecord> queryWrapper =
                buildQuotaRecordQueryWrapper(userId, interfaceInfoId, operateType, request);
        queryWrapper.orderByDesc("id");
        return userInterfaceQuotaRecordService.list(queryWrapper);
    }

    @Override
    public Page<UserInterfaceQuotaRecord> pageQuotaRecords(
            Long userId,
            Long interfaceInfoId,
            String operateType,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        QueryWrapper<UserInterfaceQuotaRecord> queryWrapper =
                buildQuotaRecordQueryWrapper(userId, interfaceInfoId, operateType, request);

        String normalizedSortField = sortField == null ? "id" : sortField.trim().toLowerCase();
        if (!ALLOWED_QUOTA_RECORD_SORT_FIELDS.contains(normalizedSortField)) {
            normalizedSortField = "id";
        }
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(true, ascending, normalizedSortField);
        return userInterfaceQuotaRecordService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    private QueryWrapper<UserInterfaceInfo> buildQueryWrapper(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            HttpServletRequest request) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (authContextService.isAdmin(request)) {
            queryWrapper.eq(userId != null, "user_id", userId);
        } else {
            queryWrapper.eq("user_id", authContextService.getLoginUser(request).getId());
        }
        queryWrapper.eq(interfaceInfoId != null, "interface_info_id", interfaceInfoId);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq("is_delete", 0);
        return queryWrapper;
    }

    private QueryWrapper<UserInterfaceQuotaRecord> buildQuotaRecordQueryWrapper(
            Long userId,
            Long interfaceInfoId,
            String operateType,
            HttpServletRequest request) {
        QueryWrapper<UserInterfaceQuotaRecord> queryWrapper = new QueryWrapper<>();
        if (authContextService.isAdmin(request)) {
            queryWrapper.eq(userId != null, "user_id", userId);
        } else {
            queryWrapper.eq("user_id", authContextService.getLoginUser(request).getId());
        }
        queryWrapper.eq(interfaceInfoId != null, "interface_info_id", interfaceInfoId);
        queryWrapper.eq(operateType != null && !operateType.isBlank(), "operate_type", operateType);
        return queryWrapper;
    }
}
