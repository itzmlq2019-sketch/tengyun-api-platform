package com.example.tengyunapibackend.service.support.impl;

import com.example.tengyunapibackend.service.UserService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.utils.AdminAuthUtils;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthContextServiceImpl implements AuthContextService {

    @Resource
    private UserService userService;

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return userService.getLoginUser(request);
    }

    @Override
    public User requireAdmin(HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        AdminAuthUtils.assertAdmin(loginUser);
        return loginUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        return AdminAuthUtils.isAdmin(getLoginUser(request));
    }

    @Override
    public void requireSelfOrAdmin(Long targetUserId, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (AdminAuthUtils.isAdmin(loginUser)) {
            return;
        }
        if (targetUserId == null || loginUser.getId() == null || !loginUser.getId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该资源");
        }
    }
}
