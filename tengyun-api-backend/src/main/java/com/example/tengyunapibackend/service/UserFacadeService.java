package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserCredentialResetRequest;
import com.example.tengyunapibackend.model.request.UserLoginRequest;
import com.example.tengyunapibackend.model.request.UserRegisterRequest;
import com.example.tengyunapibackend.model.request.UserRoleUpdateRequest;
import com.example.tengyunapibackend.model.vo.TextRepairReport;
import com.example.tengyunapibackend.model.vo.UserAdminVO;
import com.example.tengyunapibackend.model.vo.UserCredentialResetResponse;
import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserFacadeService {

    long userRegister(UserRegisterRequest request);

    User userLogin(UserLoginRequest request, HttpServletRequest httpServletRequest);

    User getCurrentUser(HttpServletRequest request);

    List<UserAdminVO> listAdminUsers(String userAccount, String userName, String userRole, HttpServletRequest request);

    Page<UserAdminVO> pageAdminUsers(
            String userAccount,
            String userName,
            String userRole,
            long pageNum,
            long pageSize,
            HttpServletRequest request);

    UserCredentialResetResponse resetCredentials(UserCredentialResetRequest request, HttpServletRequest httpServletRequest);

    boolean updateUserRole(UserRoleUpdateRequest request, HttpServletRequest httpServletRequest);

    boolean deleteUser(Long userId, HttpServletRequest httpServletRequest);

    TextRepairReport repairTextData(boolean dryRun, HttpServletRequest httpServletRequest);
}
