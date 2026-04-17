package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.tengyunapibackend.service.InterfaceInvokeLogService;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import com.example.tengyunapicommon.service.InnerInterfaceInvokeLogService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class InnerInterfaceInvokeLogServiceImpl implements InnerInterfaceInvokeLogService {

    private static final int LOG_STATUS_SUCCESS = 0;
    private static final int LOG_STATUS_FAILED = 1;
    private static final int LOG_STATUS_PROCESSING = 2;

    @Autowired
    private InterfaceInvokeLogService interfaceInvokeLogService;

    @Override
    public Long addInvokeLog(InterfaceInvokeLog interfaceInvokeLog) {
        boolean saveResult = interfaceInvokeLogService.save(interfaceInvokeLog);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用日志写入失败");
        }
        return interfaceInvokeLog.getId();
    }

    @Override
    public boolean updateInvokeLog(InterfaceInvokeLog interfaceInvokeLog) {
        return interfaceInvokeLogService.updateById(interfaceInvokeLog);
    }

    @Override
    public boolean claimInvokeLogForBilling(Long invokeLogId, Integer responseStatus, String responseMessage) {
        if (invokeLogId == null) {
            return false;
        }
        UpdateWrapper<InterfaceInvokeLog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", invokeLogId);
        updateWrapper.eq("status", LOG_STATUS_FAILED);
        updateWrapper.set("status", LOG_STATUS_PROCESSING);
        updateWrapper.set("response_status", responseStatus);
        updateWrapper.set("response_message", responseMessage);
        updateWrapper.ne("status", LOG_STATUS_SUCCESS);
        return interfaceInvokeLogService.update(updateWrapper);
    }
}
