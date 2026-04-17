package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.InterfaceInvokeLogMapper;
import com.example.tengyunapibackend.model.vo.InterfaceInvokeStatsVO;
import com.example.tengyunapibackend.model.vo.UserInvokeStatsVO;
import com.example.tengyunapibackend.service.InterfaceInvokeLogService;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InterfaceInvokeLogServiceImpl extends ServiceImpl<InterfaceInvokeLogMapper, InterfaceInvokeLog>
        implements InterfaceInvokeLogService {

    @Override
    public List<InterfaceInvokeStatsVO> listInvokeStats(Long userId, Integer limit) {
        return this.baseMapper.listInvokeStats(userId, limit);
    }

    @Override
    public Long countInvokeLogsByStatus(Integer status, Long userId) {
        return this.baseMapper.countInvokeLogsByStatus(status, userId);
    }

    @Override
    public List<InterfaceInvokeStatsVO> listInvokeStatsByInterfaceIds(List<Long> interfaceInfoIds) {
        if (interfaceInfoIds == null || interfaceInfoIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.baseMapper.listInvokeStatsByInterfaceIds(interfaceInfoIds);
    }

    @Override
    public List<UserInvokeStatsVO> listInvokeStatsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.baseMapper.listInvokeStatsByUserIds(userIds);
    }
}
