package com.example.tengyunapibackend.mapper;

import com.example.tengyunapibackend.model.vo.InterfaceQuotaStatsVO;
import com.example.tengyunapibackend.model.vo.AuthorizationSummaryVO;
import com.example.tengyunapibackend.model.vo.UserQuotaStatsVO;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author mlqnb
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2026-03-31 16:12:04
* @Entity com.example.tengyunapibackend.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<InterfaceQuotaStatsVO> listQuotaStatsByInterfaceIds(@Param("interfaceInfoIds") List<Long> interfaceInfoIds);

    List<UserQuotaStatsVO> listQuotaStatsByUserIds(@Param("userIds") List<Long> userIds);

    int consumeInvokeCount(@Param("interfaceInfoId") long interfaceInfoId, @Param("userId") long userId);

    int grantInvokeCount(@Param("id") long id, @Param("grantNum") int grantNum);

    int revokeInvokeCount(@Param("id") long id, @Param("revokeNum") int revokeNum);

    int updateAuthorizationStatus(
            @Param("userId") long userId,
            @Param("interfaceInfoId") long interfaceInfoId,
            @Param("status") int status);

    AuthorizationSummaryVO getAuthorizationSummary(
            @Param("userId") Long userId,
            @Param("interfaceInfoId") Long interfaceInfoId,
            @Param("status") Integer status);
}




