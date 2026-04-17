package com.example.tengyunapicommon.service;

public interface InnerUserInterfaceInfoService {
    // 扣钱！把 totalNum+1，leftNum-1
    boolean invokeCount(long interfaceInfoId, long userId);
}
