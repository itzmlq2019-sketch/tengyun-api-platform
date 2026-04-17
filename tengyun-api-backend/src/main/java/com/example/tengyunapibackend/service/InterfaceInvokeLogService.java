package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tengyunapibackend.model.vo.InterfaceInvokeStatsVO;
import com.example.tengyunapibackend.model.vo.UserInvokeStatsVO;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;

import java.util.List;

public interface InterfaceInvokeLogService extends IService<InterfaceInvokeLog> {

    List<InterfaceInvokeStatsVO> listInvokeStats(Long userId, Integer limit);

    Long countInvokeLogsByStatus(Integer status, Long userId);

    List<InterfaceInvokeStatsVO> listInvokeStatsByInterfaceIds(List<Long> interfaceInfoIds);

    List<UserInvokeStatsVO> listInvokeStatsByUserIds(List<Long> userIds);
}
