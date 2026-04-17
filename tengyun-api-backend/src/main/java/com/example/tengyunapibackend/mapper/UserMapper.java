package com.example.tengyunapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tengyunapibackend.model.vo.UserAdminVO;
import com.example.tengyunapicommon.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author mlqnb
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-03-31 16:12:04
* @Entity com.example.tengyunapibackend.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    Long countAdminUsers(
            @Param("userAccount") String userAccount,
            @Param("userName") String userName,
            @Param("userRole") String userRole);

    List<UserAdminVO> listAdminUsers(
            @Param("userAccount") String userAccount,
            @Param("userName") String userName,
            @Param("userRole") String userRole,
            @Param("includeSensitiveFields") boolean includeSensitiveFields,
            @Param("offset") Long offset,
            @Param("pageSize") Long pageSize);
}




