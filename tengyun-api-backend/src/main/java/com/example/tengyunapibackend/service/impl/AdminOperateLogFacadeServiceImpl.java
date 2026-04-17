package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.service.AdminOperateLogFacadeService;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.entity.AdminOperateLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminOperateLogFacadeServiceImpl implements AdminOperateLogFacadeService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "operator_user_id", "action", "target_type", "target_id", "create_time");

    @Resource
    private AdminOperateLogService adminOperateLogService;

    @Resource
    private AuthContextService authContextService;

    @Resource
    private RequestValidationService requestValidationService;

    @Override
    public Page<AdminOperateLog> pageLogs(
            Long operatorUserId,
            String action,
            String targetType,
            Long targetId,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request) {
        authContextService.requireAdmin(request);
        requestValidationService.requirePage(pageNum, pageSize, 200);
        QueryWrapper<AdminOperateLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(operatorUserId != null, "operator_user_id", operatorUserId);
        queryWrapper.eq(action != null && !action.isBlank(), "action", action);
        queryWrapper.eq(targetType != null && !targetType.isBlank(), "target_type", targetType);
        queryWrapper.eq(targetId != null, "target_id", targetId);
        queryWrapper.eq("is_delete", 0);

        String normalizedSortField = sortField == null ? "id" : sortField.trim().toLowerCase();
        if (!ALLOWED_SORT_FIELDS.contains(normalizedSortField)) {
            normalizedSortField = "id";
        }
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(true, ascending, normalizedSortField);
        return adminOperateLogService.page(new Page<>(pageNum, pageSize), queryWrapper);
    }
}
