package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tengyunapicommon.entity.AdminOperateLog;
import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminOperateLogService extends IService<AdminOperateLog> {

    void record(
            User operator,
            String action,
            String targetType,
            Long targetId,
            String detail,
            HttpServletRequest request);
}
