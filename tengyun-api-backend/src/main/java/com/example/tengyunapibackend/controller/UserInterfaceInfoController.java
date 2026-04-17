package com.example.tengyunapibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoGrantRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoRevokeRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoStatusUpdateRequest;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapibackend.service.UserInterfaceInfoFacadeService;
import com.example.tengyunapicommon.common.BaseResponse;
import com.example.tengyunapicommon.common.ResultUtils;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
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
@RequestMapping("/userInterfaceInfo")
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoFacadeService userInterfaceInfoFacadeService;

    @PostMapping("/grant")
    public BaseResponse<Boolean> grantInvokeCount(
            @RequestBody UserInterfaceInfoGrantRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userInterfaceInfoFacadeService.grantInvokeCount(request, httpServletRequest));
    }

    @PostMapping("/revoke")
    public BaseResponse<Boolean> revokeInvokeCount(
            @RequestBody UserInterfaceInfoRevokeRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userInterfaceInfoFacadeService.revokeInvokeCount(request, httpServletRequest));
    }

    @PostMapping("/status/update")
    public BaseResponse<Boolean> updateAuthorizationStatus(
            @RequestBody UserInterfaceInfoStatusUpdateRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userInterfaceInfoFacadeService.updateAuthorizationStatus(request, httpServletRequest));
    }

    @GetMapping("/get")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfo(
            @RequestParam("userId") Long userId,
            @RequestParam("interfaceInfoId") Long interfaceInfoId,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.getUserInterfaceInfo(userId, interfaceInfoId, request));
    }

    @GetMapping("/list")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "status", required = false) Integer status,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.listUserInterfaceInfo(userId, interfaceInfoId, status, request));
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<UserInterfaceInfo>> pageUserInterfaceInfo(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.pageUserInterfaceInfo(
                userId, interfaceInfoId, status, pageNum, pageSize, sortField, sortOrder, request));
    }

    @GetMapping("/admin/summary")
    public BaseResponse<AuthorizationSummaryVO> getAuthorizationSummary(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "status", required = false) Integer status,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.getAuthorizationSummary(
                userId, interfaceInfoId, status, request));
    }

    @GetMapping("/quotaRecord/list")
    public BaseResponse<List<UserInterfaceQuotaRecord>> listQuotaRecords(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "operateType", required = false) String operateType,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.listQuotaRecords(
                userId, interfaceInfoId, operateType, request));
    }

    @GetMapping("/quotaRecord/list/page")
    public BaseResponse<Page<UserInterfaceQuotaRecord>> pageQuotaRecords(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "operateType", required = false) String operateType,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoFacadeService.pageQuotaRecords(
                userId, interfaceInfoId, operateType, pageNum, pageSize, sortField, sortOrder, request));
    }
}
