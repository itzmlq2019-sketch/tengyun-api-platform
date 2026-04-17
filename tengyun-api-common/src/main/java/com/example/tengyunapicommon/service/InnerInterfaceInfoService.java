package com.example.tengyunapicommon.service;

import com.example.tengyunapicommon.entity.InterfaceInfo;

public interface InnerInterfaceInfoService {
    // 根据路径和方法，看看这个接口是不是咱平台里的
    InterfaceInfo getInterfaceInfo(String path, String method);
}