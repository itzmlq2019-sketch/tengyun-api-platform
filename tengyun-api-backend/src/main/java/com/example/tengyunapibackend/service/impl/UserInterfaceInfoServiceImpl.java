package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.UserInterfaceInfoMapper;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoGrantRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoRevokeRequest;
import com.example.tengyunapibackend.model.request.UserInterfaceInfoStatusUpdateRequest;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapibackend.model.vo.InterfaceQuotaStatsVO;
import com.example.tengyunapibackend.model.vo.UserQuotaStatsVO;
import com.example.tengyunapibackend.service.UserInterfaceInfoService;
import com.example.tengyunapibackend.service.UserInterfaceQuotaRecordService;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    private static final int STATUS_ENABLED = 0;
    private static final int STATUS_DISABLED = 1;

    @Resource
    private UserInterfaceQuotaRecordService userInterfaceQuotaRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantInvokeCount(UserInterfaceInfoGrantRequest request) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", request.getUserId());
        queryWrapper.eq("interface_info_id", request.getInterfaceInfoId());
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);

        int beforeLeftNum = 0;
        int afterLeftNum;
        boolean result;
        if (userInterfaceInfo == null) {
            userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(request.getUserId());
            userInterfaceInfo.setInterfaceInfoId(request.getInterfaceInfoId());
            userInterfaceInfo.setTotalNum(0);
            userInterfaceInfo.setLeftNum(request.getGrantNum());
            userInterfaceInfo.setStatus(STATUS_ENABLED);
            afterLeftNum = request.getGrantNum();
            result = this.save(userInterfaceInfo);
        } else {
            beforeLeftNum = userInterfaceInfo.getLeftNum() == null ? 0 : userInterfaceInfo.getLeftNum();
            int affectedRows = this.baseMapper.grantInvokeCount(userInterfaceInfo.getId(), request.getGrantNum());
            result = affectedRows > 0;
            if (result) {
                UserInterfaceInfo refreshedInfo = this.getById(userInterfaceInfo.getId());
                afterLeftNum = refreshedInfo == null || refreshedInfo.getLeftNum() == null
                        ? beforeLeftNum + request.getGrantNum()
                        : refreshedInfo.getLeftNum();
            } else {
                afterLeftNum = beforeLeftNum;
            }
        }
        if (!result) {
            return false;
        }

        UserInterfaceQuotaRecord quotaRecord = new UserInterfaceQuotaRecord();
        quotaRecord.setUserId(request.getUserId());
        quotaRecord.setInterfaceInfoId(request.getInterfaceInfoId());
        quotaRecord.setChangeNum(request.getGrantNum());
        quotaRecord.setBeforeLeftNum(beforeLeftNum);
        quotaRecord.setAfterLeftNum(afterLeftNum);
        quotaRecord.setOperateType("GRANT");
        quotaRecord.setDescription(request.getDescription());
        return userInterfaceQuotaRecordService.save(quotaRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeInvokeCount(UserInterfaceInfoRevokeRequest request) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", request.getUserId());
        queryWrapper.eq("interface_info_id", request.getInterfaceInfoId());
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户接口授权记录不存在");
        }
        int beforeLeftNum = userInterfaceInfo.getLeftNum() == null ? 0 : userInterfaceInfo.getLeftNum();
        int revokeNum = request.getRevokeNum();
        if (beforeLeftNum < revokeNum) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "回收次数不能超过当前剩余额度");
        }
        int affectedRows = this.baseMapper.revokeInvokeCount(userInterfaceInfo.getId(), revokeNum);
        if (affectedRows <= 0) {
            return false;
        }
        UserInterfaceInfo refreshedInfo = this.getById(userInterfaceInfo.getId());
        int afterLeftNum = refreshedInfo == null || refreshedInfo.getLeftNum() == null
                ? beforeLeftNum - revokeNum
                : refreshedInfo.getLeftNum();

        UserInterfaceQuotaRecord quotaRecord = new UserInterfaceQuotaRecord();
        quotaRecord.setUserId(request.getUserId());
        quotaRecord.setInterfaceInfoId(request.getInterfaceInfoId());
        quotaRecord.setChangeNum(-revokeNum);
        quotaRecord.setBeforeLeftNum(beforeLeftNum);
        quotaRecord.setAfterLeftNum(afterLeftNum);
        quotaRecord.setOperateType("REVOKE");
        quotaRecord.setDescription(request.getDescription());
        return userInterfaceQuotaRecordService.save(quotaRecord);
    }

    @Override
    public boolean updateAuthorizationStatus(UserInterfaceInfoStatusUpdateRequest request) {
        Integer status = request.getStatus();
        if (status == null || (status != STATUS_ENABLED && status != STATUS_DISABLED)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "status only supports 0/1");
        }
        return this.baseMapper.updateAuthorizationStatus(
                request.getUserId(),
                request.getInterfaceInfoId(),
                status) > 0;
    }

    @Override
    public AuthorizationSummaryVO getAuthorizationSummary(Long userId, Long interfaceInfoId, Integer status) {
        if (status != null && status != STATUS_ENABLED && status != STATUS_DISABLED) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "status only supports 0/1");
        }
        return this.baseMapper.getAuthorizationSummary(userId, interfaceInfoId, status);
    }

    @Override
    public List<InterfaceQuotaStatsVO> listQuotaStatsByInterfaceIds(List<Long> interfaceInfoIds) {
        if (interfaceInfoIds == null || interfaceInfoIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.baseMapper.listQuotaStatsByInterfaceIds(interfaceInfoIds);
    }

    @Override
    public List<UserQuotaStatsVO> listQuotaStatsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.baseMapper.listQuotaStatsByUserIds(userIds);
    }
}
