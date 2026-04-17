package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.vo.InterfaceInvokeStatsVO;
import com.example.tengyunapibackend.model.vo.InvokeStatsResponse;
import com.example.tengyunapibackend.service.InterfaceInvokeLogFacadeService;
import com.example.tengyunapibackend.service.InterfaceInvokeLogService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class InterfaceInvokeLogFacadeServiceImpl implements InterfaceInvokeLogFacadeService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "status", "response_status", "create_time", "update_time");

    @Resource
    private InterfaceInvokeLogService interfaceInvokeLogService;

    @Resource
    private AuthContextService authContextService;

    @Resource
    private RequestValidationService requestValidationService;

    @Override
    public InterfaceInvokeLog getInvokeLog(Long id, HttpServletRequest request) {
        requestValidationService.requirePositiveId(id, "id");
        User loginUser = authContextService.getLoginUser(request);
        boolean admin = authContextService.isAdmin(request);
        InterfaceInvokeLog invokeLog = interfaceInvokeLogService.getById(id);
        if (invokeLog == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "调用日志不存在");
        }
        if (!admin && !loginUser.getId().equals(invokeLog.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权查看其他用户的调用日志");
        }
        return invokeLog;
    }

    @Override
    public List<InterfaceInvokeLog> listInvokeLog(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            Date startTime,
            Date endTime,
            HttpServletRequest request) {
        requestValidationService.requireTimeRange(startTime, endTime, "startTime cannot be after endTime");
        QueryWrapper<InterfaceInvokeLog> queryWrapper = buildQueryWrapper(
                userId, interfaceInfoId, status, startTime, endTime, request);
        queryWrapper.orderByDesc("id");
        return interfaceInvokeLogService.list(queryWrapper);
    }

    @Override
    public Page<InterfaceInvokeLog> pageInvokeLog(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            Date startTime,
            Date endTime,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        requestValidationService.requireTimeRange(startTime, endTime, "startTime cannot be after endTime");
        QueryWrapper<InterfaceInvokeLog> queryWrapper = buildQueryWrapper(
                userId, interfaceInfoId, status, startTime, endTime, request);

        String normalizedSortField = sortField == null ? "id" : sortField.trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            normalizedSortField = "id";
        }
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(true, ascending, normalizedSortField);
        return interfaceInvokeLogService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public InvokeStatsResponse getInvokeStats(Long userId, Integer limit, HttpServletRequest request) {
        requestValidationService.requirePositiveNumber(limit, "limit");
        if (limit > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "limit too large");
        }
        Long targetUserId = authContextService.isAdmin(request)
                ? userId
                : authContextService.getLoginUser(request).getId();

        List<InterfaceInvokeStatsVO> interfaceStats = interfaceInvokeLogService.listInvokeStats(targetUserId, limit);
        Long successInvokeCount = interfaceInvokeLogService.countInvokeLogsByStatus(0, targetUserId);
        Long failedInvokeCount = interfaceInvokeLogService.countInvokeLogsByStatus(1, targetUserId);

        InvokeStatsResponse response = new InvokeStatsResponse();
        response.setSuccessInvokeCount(successInvokeCount);
        response.setFailedInvokeCount(failedInvokeCount);
        response.setTotalInvokeCount(successInvokeCount + failedInvokeCount);
        response.setInterfaceStats(interfaceStats);
        return response;
    }

    private QueryWrapper<InterfaceInvokeLog> buildQueryWrapper(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            Date startTime,
            Date endTime,
            HttpServletRequest request) {
        QueryWrapper<InterfaceInvokeLog> queryWrapper = new QueryWrapper<>();
        if (authContextService.isAdmin(request)) {
            queryWrapper.eq(userId != null, "user_id", userId);
        } else {
            queryWrapper.eq("user_id", authContextService.getLoginUser(request).getId());
        }
        queryWrapper.eq(interfaceInfoId != null, "interface_info_id", interfaceInfoId);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.ge(startTime != null, "create_time", startTime);
        queryWrapper.le(endTime != null, "create_time", endTime);
        return queryWrapper;
    }
}
