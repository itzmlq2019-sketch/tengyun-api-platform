package com.example.tengyunapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.tengyunapibackend.model.vo.InterfaceInvokeStatsVO;
import com.example.tengyunapibackend.model.vo.UserInvokeStatsVO;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterfaceInvokeLogMapper extends BaseMapper<InterfaceInvokeLog> {

    List<InterfaceInvokeStatsVO> listInvokeStats(@Param("userId") Long userId, @Param("limit") Integer limit);

    Long countInvokeLogsByStatus(@Param("status") Integer status, @Param("userId") Long userId);

    List<InterfaceInvokeStatsVO> listInvokeStatsByInterfaceIds(@Param("interfaceInfoIds") List<Long> interfaceInfoIds);

    List<UserInvokeStatsVO> listInvokeStatsByUserIds(@Param("userIds") List<Long> userIds);
}
