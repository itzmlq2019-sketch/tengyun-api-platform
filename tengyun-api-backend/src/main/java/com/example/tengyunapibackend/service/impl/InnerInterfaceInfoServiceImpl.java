package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tengyunapibackend.mapper.InterfaceInfoMapper;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.service.InnerInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", path);
        queryWrapper.eq("method", method);
        queryWrapper.eq("status", 1);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
