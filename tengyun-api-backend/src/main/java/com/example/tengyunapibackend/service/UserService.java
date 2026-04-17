package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tengyunapicommon.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    /**
     * 管理操作：重置用户 accessKey / secretKey，返回重置后的用户信息（包含新 AK/SK）。
     */
    User resetCredentials(Long userId, boolean resetAccessKey, boolean resetSecretKey);
}
