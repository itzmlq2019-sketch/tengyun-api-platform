package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tengyunapibackend.mapper.UserInterfaceInfoMapper;
import com.example.tengyunapibackend.service.UserInterfaceInfoService;
import com.example.tengyunapibackend.service.UserInterfaceQuotaRecordService;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
import com.example.tengyunapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@DubboService
@Service
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    private static final int STATUS_ENABLED = 0;

    @Autowired
    private UserInterfaceInfoService userInterfaceInfoService;

    @Autowired
    private UserInterfaceQuotaRecordService userInterfaceQuotaRecordService;

    @Autowired
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public boolean hasInvokeCount(long interfaceInfoId, long userId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_info_id", interfaceInfoId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("status", STATUS_ENABLED);
        queryWrapper.eq("is_delete", 0);
        queryWrapper.gt("left_num", 0);
        return userInterfaceInfoService.count(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean invokeCount(long interfaceInfoId, long userId) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_info_id", interfaceInfoId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_delete", 0);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null || userInterfaceInfo.getLeftNum() == null || userInterfaceInfo.getLeftNum() <= 0) {
            return false;
        }
        int affectedRows = userInterfaceInfoMapper.consumeInvokeCount(interfaceInfoId, userId);
        if (affectedRows <= 0) {
            return false;
        }
        UserInterfaceInfo refreshedInfo = userInterfaceInfoService.getById(userInterfaceInfo.getId());
        if (refreshedInfo == null || refreshedInfo.getLeftNum() == null) {
            return false;
        }
        int afterLeftNum = refreshedInfo.getLeftNum();
        int beforeLeftNum = afterLeftNum + 1;

        UserInterfaceQuotaRecord quotaRecord = new UserInterfaceQuotaRecord();
        quotaRecord.setUserId(userId);
        quotaRecord.setInterfaceInfoId(interfaceInfoId);
        quotaRecord.setChangeNum(-1);
        quotaRecord.setBeforeLeftNum(beforeLeftNum);
        quotaRecord.setAfterLeftNum(afterLeftNum);
        quotaRecord.setOperateType("CONSUME");
        quotaRecord.setDescription("invoke consume");
        return userInterfaceQuotaRecordService.save(quotaRecord);
    }
}
