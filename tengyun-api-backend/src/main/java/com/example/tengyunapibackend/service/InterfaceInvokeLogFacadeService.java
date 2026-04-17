package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.vo.InvokeStatsResponse;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

public interface InterfaceInvokeLogFacadeService {

    InterfaceInvokeLog getInvokeLog(Long id, HttpServletRequest request);

    List<InterfaceInvokeLog> listInvokeLog(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            Date startTime,
            Date endTime,
            HttpServletRequest request);

    Page<InterfaceInvokeLog> pageInvokeLog(
            Long userId,
            Long interfaceInfoId,
            Integer status,
            Date startTime,
            Date endTime,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder,
            HttpServletRequest request);

    InvokeStatsResponse getInvokeStats(Long userId, Integer limit, HttpServletRequest request);
}
