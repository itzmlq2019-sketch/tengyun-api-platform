package com.example.tengyunapibackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.InterfaceInfoAddRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoInvokeRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoUpdateRequest;
import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;
import com.example.tengyunapibackend.service.InterfaceInfoFacadeService;
import com.example.tengyunapicommon.common.BaseResponse;
import com.example.tengyunapicommon.common.ResultUtils;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoFacadeService interfaceInfoFacadeService;

    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest request, HttpServletRequest context) {
        return ResultUtils.success(interfaceInfoFacadeService.addInterfaceInfo(request, context));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceInfo(
            @RequestBody InterfaceInfoUpdateRequest request,
            HttpServletRequest context) {
        return ResultUtils.success(interfaceInfoFacadeService.updateInterfaceInfo(request, context));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestParam("id") Long id, HttpServletRequest context) {
        return ResultUtils.success(interfaceInfoFacadeService.deleteInterfaceInfo(id, context));
    }

    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfo(@RequestParam("id") Long id) {
        return ResultUtils.success(interfaceInfoFacadeService.getInterfaceInfo(id));
    }

    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "name", required = false) String name) {
        return ResultUtils.success(interfaceInfoFacadeService.listInterfaceInfo(method, status, name));
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> pageInterfaceInfo(
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        return ResultUtils.success(interfaceInfoFacadeService.pageInterfaceInfo(
                method, status, name, pageNum, pageSize, sortField, sortOrder));
    }

    @GetMapping("/admin/list")
    public BaseResponse<List<InterfaceAdminVO>> listAdminInterfaceInfo(
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "name", required = false) String name,
            HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoFacadeService.listAdminInterfaceInfo(method, status, name, request));
    }

    @GetMapping("/admin/list/page")
    public BaseResponse<Page<InterfaceAdminVO>> pageAdminInterfaceInfo(
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageNum", defaultValue = "1") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoFacadeService.pageAdminInterfaceInfo(
                method, status, name, pageNum, pageSize, request));
    }

    @PostMapping("/online")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestParam("id") Long id, HttpServletRequest context) {
        return ResultUtils.success(interfaceInfoFacadeService.onlineInterfaceInfo(id, context));
    }

    @PostMapping("/offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestParam("id") Long id, HttpServletRequest context) {
        return ResultUtils.success(interfaceInfoFacadeService.offlineInterfaceInfo(id, context));
    }

    @PostMapping("/invoke")
    public BaseResponse<String> invokeInterface(
            @RequestBody InterfaceInfoInvokeRequest request,
            HttpServletRequest httpServletRequest) {
        return ResultUtils.success(interfaceInfoFacadeService.invokeInterface(request, httpServletRequest));
    }
}
