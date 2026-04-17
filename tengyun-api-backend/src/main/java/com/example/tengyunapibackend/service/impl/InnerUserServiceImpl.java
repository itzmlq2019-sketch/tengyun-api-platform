package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.tengyunapibackend.mapper.UserMapper;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.service.InnerUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DubboService // 🌟 别忘了这是 Dubbo 的注解
@Service
public class InnerUserServiceImpl implements InnerUserService {

    @Autowired
    private UserMapper userMapper; // 注入之前自动生成的 Mapper

    @Override
    public User getInvokeUser(String accessKey) {
        // 🛡️ 核心：去数据库里找这个 accessKey 对应的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_key", accessKey); // 🌟 注意：数据库里是带下划线的

        // 直接返回查到的 User 实体（包含 secretKey）
        return userMapper.selectOne(queryWrapper);
    }
}