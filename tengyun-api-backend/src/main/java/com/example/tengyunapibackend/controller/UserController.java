package com.example.tengyunapibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserCredentialResetRequest;
import com.example.tengyunapibackend.model.request.UserLoginRequest;
import com.example.tengyunapibackend.model.request.UserRegisterRequest;
import com.example.tengyunapibackend.model.request.UserRoleUpdateRequest;
import com.example.tengyunapibackend.model.vo.TextRepairReport;
import com.example.tengyunapibackend.model.vo.UserAdminVO;
import com.example.tengyunapibackend.model.vo.UserCredentialResetResponse;
import com.example.tengyunapibackend.service.UserFacadeService;
import com.example.tengyunapicommon.common.BaseResponse;
import com.example.tengyunapicommon.common.ResultUtils;
import com.example.tengyunapicommon.entity.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserFacadeService userFacadeService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest request) {
        return ResultUtils.success(userFacadeService.userRegister(request));
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        return ResultUtils.success(userFacadeService.userLogin(loginRequest, request));
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        return ResultUtils.success(userFacadeService.getCurrentUser(request));
    }

    @GetMapping("/admin/list")
    public BaseResponse<List<UserAdminVO>> listAdminUsers(
            @RequestParam(value = "userAccount", required = false) String userAccount,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "userRole", required = false) String userRole,
            HttpServletRequest request) {
        return ResultUtils.success(userFacadeService.listAdminUsers(userAccount, userName, userRole, request));
    }

    @GetMapping("/admin/list/page")
    public BaseResponse<Page<UserAdminVO>> pageAdminUsers(
            @RequestParam(value = "userAccount", required = false) String userAccount,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "userRole", required = false) String userRole,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        return ResultUtils.success(userFacadeService.pageAdminUsers(
                userAccount, userName, userRole, pageNum, pageSize, request));
    }

    @PostMapping("/admin/resetCredentials")
    public BaseResponse<UserCredentialResetResponse> resetCredentials(
            @RequestBody UserCredentialResetRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userFacadeService.resetCredentials(request, httpServletRequest));
    }

    @PostMapping("/admin/updateRole")
    public BaseResponse<Boolean> updateUserRole(
            @RequestBody UserRoleUpdateRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userFacadeService.updateUserRole(request, httpServletRequest));
    }

    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> deleteUser(
            @RequestParam("userId") Long userId,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userFacadeService.deleteUser(userId, httpServletRequest));
    }

    @PostMapping("/admin/repairTextData")
    public BaseResponse<TextRepairReport> repairTextData(
            @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userFacadeService.repairTextData(dryRun, httpServletRequest));
    }
}
