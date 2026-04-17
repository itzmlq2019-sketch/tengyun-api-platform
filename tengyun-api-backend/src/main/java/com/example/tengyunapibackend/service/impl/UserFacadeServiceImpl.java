package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserCredentialResetRequest;
import com.example.tengyunapibackend.model.request.UserLoginRequest;
import com.example.tengyunapibackend.model.request.UserRegisterRequest;
import com.example.tengyunapibackend.model.request.UserRoleUpdateRequest;
import com.example.tengyunapibackend.model.vo.TextRepairReport;
import com.example.tengyunapibackend.model.vo.UserAdminVO;
import com.example.tengyunapibackend.model.vo.UserCredentialResetResponse;
import com.example.tengyunapibackend.service.AdminOperateLogService;
import com.example.tengyunapibackend.service.TextDataRepairService;
import com.example.tengyunapibackend.service.UserAdminQueryService;
import com.example.tengyunapibackend.service.UserFacadeService;
import com.example.tengyunapibackend.service.UserService;
import com.example.tengyunapibackend.service.support.AuthContextService;
import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFacadeServiceImpl implements UserFacadeService {

    @Resource
    private UserService userService;

    @Resource
    private UserAdminQueryService userAdminQueryService;

    @Resource
    private TextDataRepairService textDataRepairService;

    @Resource
    private AdminOperateLogService adminOperateLogService;

    @Resource
    private AuthContextService authContextService;

    @Resource
    private RequestValidationService requestValidationService;

    @Override
    public long userRegister(UserRegisterRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.userRegister(
                request.getUserAccount(),
                request.getUserPassword(),
                request.getCheckPassword());
    }

    @Override
    public User userLogin(UserLoginRequest request, HttpServletRequest httpServletRequest) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.userLogin(request.getUserAccount(), request.getUserPassword(), httpServletRequest);
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        return authContextService.getLoginUser(request);
    }

    @Override
    public List<UserAdminVO> listAdminUsers(
            String userAccount,
            String userName,
            String userRole,
            HttpServletRequest request) {
        boolean includeSensitiveFields = authContextService.isAdmin(request);
        return userAdminQueryService.listUsers(userAccount, userName, userRole, includeSensitiveFields);
    }

    @Override
    public Page<UserAdminVO> pageAdminUsers(
            String userAccount,
            String userName,
            String userRole,
            long pageNum,
            long pageSize,
            HttpServletRequest request) {
        requestValidationService.requirePage(pageNum, pageSize, 200);
        boolean includeSensitiveFields = authContextService.isAdmin(request);
        return userAdminQueryService.pageUsers(
                userAccount, userName, userRole, pageNum, pageSize, includeSensitiveFields);
    }

    @Override
    public UserCredentialResetResponse resetCredentials(
            UserCredentialResetRequest request,
            HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getUserId(), "userId");
        boolean resetAccessKey = Boolean.TRUE.equals(request.getResetAccessKey());
        boolean resetSecretKey = Boolean.TRUE.equals(request.getResetSecretKey());
        if (!resetAccessKey && !resetSecretKey) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "至少选择重置 accessKey 或 secretKey");
        }

        User updatedUser = userService.resetCredentials(request.getUserId(), resetAccessKey, resetSecretKey);
        UserCredentialResetResponse response = new UserCredentialResetResponse();
        response.setUserId(updatedUser.getId());
        response.setAccessKey(resetAccessKey ? updatedUser.getAccessKey() : null);
        response.setSecretKey(resetSecretKey ? updatedUser.getSecretKey() : null);
        adminOperateLogService.record(
                admin,
                "USER_RESET_CREDENTIALS",
                "user",
                request.getUserId(),
                "resetAccessKey=" + resetAccessKey + ",resetSecretKey=" + resetSecretKey,
                httpServletRequest);
        return response;
    }

    @Override
    public boolean updateUserRole(UserRoleUpdateRequest request, HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        requestValidationService.requirePositiveId(request.getUserId(), "userId");
        String userRole = request.getUserRole();
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userRole 不能为空");
        }
        String normalizedRole = userRole.trim().toLowerCase();
        if (!"user".equals(normalizedRole) && !"admin".equals(normalizedRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userRole 仅支持 user/admin");
        }

        User toUpdate = new User();
        toUpdate.setId(request.getUserId());
        toUpdate.setUserRole(normalizedRole);
        boolean updated = userService.updateById(toUpdate);
        if (updated) {
            adminOperateLogService.record(
                    admin,
                    "USER_UPDATE_ROLE",
                    "user",
                    request.getUserId(),
                    "newRole=" + normalizedRole,
                    httpServletRequest);
        }
        return updated;
    }

    @Override
    public boolean deleteUser(Long userId, HttpServletRequest httpServletRequest) {
        User loginUser = authContextService.requireAdmin(httpServletRequest);
        requestValidationService.requirePositiveId(userId, "userId");
        if (loginUser.getId() != null && loginUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能删除自己");
        }
        boolean removed = userService.removeById(userId);
        if (removed) {
            adminOperateLogService.record(
                    loginUser,
                    "USER_DELETE",
                    "user",
                    userId,
                    "delete user",
                    httpServletRequest);
        }
        return removed;
    }

    @Override
    public TextRepairReport repairTextData(boolean dryRun, HttpServletRequest httpServletRequest) {
        User admin = authContextService.requireAdmin(httpServletRequest);
        TextRepairReport report = textDataRepairService.repairMojibake(dryRun);
        adminOperateLogService.record(
                admin,
                "TEXT_REPAIR",
                "system",
                0L,
                "dryRun=" + dryRun + ",totalUpdatedCount=" + report.getTotalUpdatedCount(),
                httpServletRequest);
        return report;
    }
}
