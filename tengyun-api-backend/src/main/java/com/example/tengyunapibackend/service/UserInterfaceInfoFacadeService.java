package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoGrantRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoRevokeRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoStatusUpdateRequest;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserInterfaceInfoFacadeService {

    boolean grantInvokeCount(UserInterfaceInfoGrantRequest request, HttpServletRequest httpServletRequest);

    boolean revokeInvokeCount(UserInterfaceInfoRevokeRequest request, HttpServletRequest httpServletRequest);

    boolean updateAuthorizationStatus(UserInterfaceInfoStatusUpdateRequest request, HttpServletRequest httpServletRequest);

    UserInterfaceInfo getUserInterfaceInfo(Long userId, Long interfaceInfoId, HttpServletRequest request);

    List<UserInterfaceInfo> listUserInterfaceInfo(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            HttpServletRequest request);

    Page<UserInterfaceInfo> pageUserInterfaceInfo(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request);

    AuthorizationSummaryVO getAuthorizationSummary(Long userId, Long interfaceInfoId, Integer status, HttpServletRequest request);

    List<UserInterfaceQuotaRecord> listQuotaRecords(
            Long userId,
            Long interfaceInfoId,
            String operateType,
            HttpServletRequest request);

    Page<UserInterfaceQuotaRecord> pageQuotaRecords(
            Long userId,
            Long interfaceInfoId,
            String operateType,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request);
}
