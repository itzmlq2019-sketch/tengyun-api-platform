package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.vo.UserAdminVO;

import java.util.List;

public interface UserAdminQueryService {

    List<UserAdminVO> listUsers(String userAccount, String userName, String userRole, boolean includeSensitiveFields);

    Page<UserAdminVO> pageUsers(
            String userAccount,
            String userName,
            String userRole,
            long pageNum,
            long pageSize,
            boolean includeSensitiveFields);
}
