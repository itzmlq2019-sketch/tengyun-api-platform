package com.example.tengyunapibackend.service.support;

import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthContextService {

    User getLoginUser(HttpServletRequest request);

    User requireAdmin(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    void requireSelfOrAdmin(Long targetUserId, HttpServletRequest request);
}
