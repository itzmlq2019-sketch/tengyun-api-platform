package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.mapper.UserMapper;
import com.example.tengyunapibackend.model.vo.UserAdminVO;
import com.example.tengyunapibackend.service.UserAdminQueryService;
import com.example.tengyunapibackend.utils.TextFixUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminQueryServiceImpl implements UserAdminQueryService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<UserAdminVO> listUsers(
            String userAccount,
            String userName,
            String userRole,
            boolean includeSensitiveFields) {
        List<UserAdminVO> result = userMapper.listAdminUsers(
                userAccount, userName, userRole, includeSensitiveFields, null, null);
        fixTextFields(result);
        return result;
    }

    @Override
    public Page<UserAdminVO> pageUsers(
            String userAccount,
            String userName,
            String userRole,
            long pageNum,
            long pageSize,
            boolean includeSensitiveFields) {
        long total = userMapper.countAdminUsers(userAccount, userName, userRole);
        Page<UserAdminVO> page = new Page<>(pageNum, pageSize, total);
        if (total <= 0) {
            page.setRecords(List.of());
            return page;
        }
        long offset = (pageNum - 1) * pageSize;
        List<UserAdminVO> records = userMapper.listAdminUsers(
                userAccount, userName, userRole, includeSensitiveFields, offset, pageSize);
        fixTextFields(records);
        page.setRecords(records);
        return page;
    }

    private void fixTextFields(List<UserAdminVO> records) {
        for (UserAdminVO vo : records) {
            vo.setUserName(TextFixUtils.fixMojibake(vo.getUserName()));
        }
    }
}
