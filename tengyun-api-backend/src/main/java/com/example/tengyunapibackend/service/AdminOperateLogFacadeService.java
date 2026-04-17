package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapicommon.entity.AdminOperateLog;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminOperateLogFacadeService {

    Page<AdminOperateLog> pageLogs(
            Long operatorUserId,
            String action,
            String targetType,
            Long targetId,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request);
}
