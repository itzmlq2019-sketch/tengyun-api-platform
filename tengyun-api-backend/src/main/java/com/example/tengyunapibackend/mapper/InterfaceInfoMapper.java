package com.example.tengyunapibackend.mapper;

import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author mlqnb
* @description 针对表【interface_info(接口信息表)】的数据库操作Mapper
* @createDate 2026-03-31 16:12:04
* @Entity com.example.tengyunapibackend.entity.InterfaceInfo
*/
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {

    Long countAdminInterfaces(
            @Param("method") String method,
            @Param("status") Integer status,
            @Param("name") String name);

    List<InterfaceAdminVO> listAdminInterfaces(
            @Param("method") String method,
            @Param("status") Integer status,
            @Param("name") String name,
            @Param("includeSensitiveFields") boolean includeSensitiveFields,
            @Param("offset") Long offset,
            @Param("pageSize") Long pageSize);
}




