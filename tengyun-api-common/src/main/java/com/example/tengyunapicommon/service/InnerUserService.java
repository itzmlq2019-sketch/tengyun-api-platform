package com.example.tengyunapicommon.service;

import com.example.tengyunapicommon.entity.User;

/**
 * 内部用户服务（仅供微服务内部调用，不暴露给前端）
 */
public interface InnerUserService {

    /**
     * 数据库里到底有没有这个分配给开发者的 AccessKey？
     * 如果有，把这个 User 的完整信息（包含极其机密的 SecretKey）查出来返回给网关！
     *
     * @param accessKey 开发者公钥
     * @return 包含 SK 的用户信息
     */
    User getInvokeUser(String accessKey);
}