package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tengyunapibackend.model.vo.InterfaceQuotaStatsVO;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoGrantRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoRevokeRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoStatusUpdateRequest;
import com.example.tengyunapibackend.model.vo.UserQuotaStatsVO;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author mlqnb
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2026-03-31 16:12:04
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    boolean grantInvokeCount(UserInterfaceInfoGrantRequest request);

    boolean revokeInvokeCount(UserInterfaceInfoRevokeRequest request);

    boolean updateAuthorizationStatus(UserInterfaceInfoStatusUpdateRequest request);

    AuthorizationSummaryVO getAuthorizationSummary(Long userId, Long interfaceInfoId, Integer status);

    List<InterfaceQuotaStatsVO> listQuotaStatsByInterfaceIds(List<Long> interfaceInfoIds);

    List<UserQuotaStatsVO> listQuotaStatsByUserIds(List<Long> userIds);
}
