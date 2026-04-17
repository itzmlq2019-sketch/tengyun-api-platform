package com.example.tengyunapibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.service.AdminOperateLogFacadeService;
import com.example.tengyunapicommon.common.BaseResponse;
import com.example.tengyunapicommon.common.ResultUtils;
import com.example.tengyunapicommon.entity.AdminOperateLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminOperateLog")
public class AdminOperateLogController {

    @Resource
    private AdminOperateLogFacadeService adminOperateLogFacadeService;

    @GetMapping("/list/page")
    public BaseResponse<Page<AdminOperateLog>> pageLogs(
            @RequestParam(value = "operatorUserId", required = false) Long operatorUserId,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "targetType", required = false) String targetType,
            @RequestParam(value = "targetId", required = false) Long targetId,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
            HttpServletRequest request) {
        return ResultUtils.success(adminOperateLogFacadeService.pageLogs(
                operatorUserId, action, targetType, targetId, pageNum, pageSize, sortField, sortOrder, request));
    }
}
