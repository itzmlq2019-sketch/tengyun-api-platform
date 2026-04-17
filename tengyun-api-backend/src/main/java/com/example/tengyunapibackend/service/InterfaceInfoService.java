package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tengyunapicommon.entity.InterfaceInfo;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

    boolean deleteInterfaceInfo(Long id);

    boolean updateInterfaceStatus(Long id, int status);
}
