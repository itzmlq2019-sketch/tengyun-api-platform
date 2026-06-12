package com.example.tengyunapicommon.service;

import com.example.tengyunapicommon.entity.InterfaceInvokeLog;

public interface InnerInterfaceInvokeLogService {

    Long addInvokeLog(InterfaceInvokeLog interfaceInvokeLog);

    boolean updateInvokeLog(InterfaceInvokeLog interfaceInvokeLog);

    boolean claimInvokeLogForBilling(Long invokeLogId, Integer responseStatus, String responseMessage);
}
