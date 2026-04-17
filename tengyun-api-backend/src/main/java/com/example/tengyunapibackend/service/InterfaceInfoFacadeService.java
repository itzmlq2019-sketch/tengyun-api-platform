package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.request.InterfaceInfoAddRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoInvokeRequest;
import com.example.tengyunapibackend.model.request.InterfaceInfoUpdateRequest;
import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface InterfaceInfoFacadeService {

    long addInterfaceInfo(InterfaceInfoAddRequest request, HttpServletRequest requestContext);

    boolean updateInterfaceInfo(InterfaceInfoUpdateRequest request, HttpServletRequest requestContext);

    boolean deleteInterfaceInfo(Long id, HttpServletRequest requestContext);

    InterfaceInfo getInterfaceInfo(Long id);

    List<InterfaceInfo> listInterfaceInfo(String method, Integer status, String name);

    Page<InterfaceInfo> pageInterfaceInfo(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            String sortField,
            String sortOrder);

    List<InterfaceAdminVO> listAdminInterfaceInfo(
            String method,
            Integer status,
            String name,
            HttpServletRequest request);

    Page<InterfaceAdminVO> pageAdminInterfaceInfo(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            HttpServletRequest request);

    boolean onlineInterfaceInfo(Long id, HttpServletRequest requestContext);

    boolean offlineInterfaceInfo(Long id, HttpServletRequest requestContext);

    String invokeInterface(InterfaceInfoInvokeRequest request, HttpServletRequest requestContext);
}
