package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.UserMapper;
import com.example.tengyunapibackend.service.UserService;
import com.example.tengyunapicommon.entity.User;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "tengyun_salt_2026";
    private static final String DEFAULT_USER_ROLE = "user";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (userAccount == null || userAccount.trim().isEmpty()
                || userPassword == null || userPassword.trim().isEmpty()
                || checkPassword == null || checkPassword.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于 4 位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 8 位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAccount));
        if (existUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userAccount);
        user.setUserRole(DEFAULT_USER_ROLE);
        user.setAccessKey(generateCredential("tengyun_ak_"));
        user.setSecretKey(generateCredential("tengyun_sk_"));

        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库异常");
        }
        return user.getId();
    }

    private String generateCredential(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (userAccount == null || userAccount.trim().isEmpty()
                || userPassword == null || userPassword.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }

        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserAccount, userAccount));
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        String storedPassword = user.getUserPassword();
        boolean passwordMatched = encryptPassword.equals(storedPassword);

        // 兼容历史明文密码：首次登录成功后自动升级为加盐 MD5。
        if (!passwordMatched && userPassword.equals(storedPassword)) {
            passwordMatched = true;
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setUserPassword(encryptPassword);
            this.updateById(updateUser);
            user.setUserPassword(encryptPassword);
        }

        if (!passwordMatched) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        request.getSession().setAttribute("user", user);
        user.setUserPassword(null);
        return user;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute("user");
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        return (User) userObj;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User resetCredentials(Long userId, boolean resetAccessKey, boolean resetSecretKey) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!resetAccessKey && !resetSecretKey) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "至少选择重置 accessKey 或 secretKey");
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (resetAccessKey) {
            user.setAccessKey(generateCredential("tengyun_ak_"));
        }
        if (resetSecretKey) {
            user.setSecretKey(generateCredential("tengyun_sk_"));
        }
        boolean updated = this.updateById(user);
        if (!updated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "重置失败");
        }
        return user;
    }
}
