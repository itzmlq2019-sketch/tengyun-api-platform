package com.example.tengyunapibackend.utils;

import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;

public class AdminAuthUtils {

    private AdminAuthUtils() {
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getUserRole() != null && "admin".equalsIgnoreCase(user.getUserRole());
    }

    public static void assertAdmin(User user) {
        if (!isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可访问");
        }
    }
}

