package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.AdminOperateLogMapper;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapicommon.entity.AdminOperateLog;
import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminOperateLogServiceImpl extends ServiceImpl<AdminOperateLogMapper, AdminOperateLog>
        implements AdminOperateLogService {

    @Override
    public void record(
            User operator,
            String action,
            String targetType,
            Long targetId,
            String detail,
            HttpServletRequest request) {
        if (operator == null || operator.getId() == null || action == null || action.isBlank()) {
            return;
        }
        AdminOperateLog log = new AdminOperateLog();
        log.setOperatorUserId(operator.getId());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        if (request != null) {
            log.setRequestPath(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            log.setRequestIp(extractClientIp(request));
        }
        this.save(log);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
