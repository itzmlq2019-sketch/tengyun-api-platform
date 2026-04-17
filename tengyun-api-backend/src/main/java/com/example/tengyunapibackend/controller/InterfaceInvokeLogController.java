package com.example.tengyunapibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.vo.InvokeStatsResponse;
import com.example.tengyunapibackend.service.InterfaceInvokeLogFacadeService;
import com.example.tengyunapicommon.common.BaseResponse;
import com.example.tengyunapicommon.common.ResultUtils;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/interfaceInvokeLog")
public class InterfaceInvokeLogController {

    @Resource
    private InterfaceInvokeLogFacadeService interfaceInvokeLogFacadeService;

    @GetMapping("/get")
    public BaseResponse<InterfaceInvokeLog> getInvokeLog(@RequestParam("id") Long id, HttpServletRequest request) {
        return ResultUtils.success(interfaceInvokeLogFacadeService.getInvokeLog(id, request));
    }

    @GetMapping("/list")
    public BaseResponse<List<InterfaceInvokeLog>> listInvokeLog(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            HttpServletRequest request) {
        return ResultUtils.success(interfaceInvokeLogFacadeService.listInvokeLog(
                userId, interfaceInfoId, status, startTime, endTime, request));
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInvokeLog>> pageInvokeLog(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "interfaceInfoId", required = false) Long interfaceInfoId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        return ResultUtils.success(interfaceInvokeLogFacadeService.pageInvokeLog(
                userId, interfaceInfoId, status, startTime, endTime, pageNum, pageSize, sortField, sortOrder, request));
    }

    @GetMapping("/stats")
    public BaseResponse<InvokeStatsResponse> getInvokeStats(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        return ResultUtils.success(interfaceInvokeLogFacadeService.getInvokeStats(userId, limit, request));
    }
}
