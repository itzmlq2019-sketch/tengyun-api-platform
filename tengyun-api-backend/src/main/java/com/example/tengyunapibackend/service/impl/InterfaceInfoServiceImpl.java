package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.InterfaceInfoMapper;
import com.example.tengyunapibackend.service.InterfaceInfoService;
import com.example.tengyunapibackend.service.InterfaceInvokeLogService;
import com.example.tengyunapibackend.service.UserInterfaceInfoService;
import com.example.tengyunapicommon.entity.InterfaceInfo;
import com.example.tengyunapicommon.entity.InterfaceInvokeLog;
import com.example.tengyunapicommon.entity.UserInterfaceInfo;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    private static final int STATUS_OFFLINE = 0;
    private static final int STATUS_ONLINE = 1;
    private static final Set<String> SUPPORTED_METHODS = Set.of("GET", "POST");

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private InterfaceInvokeLogService interfaceInvokeLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInterfaceInfo(Long id) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        if (Integer.valueOf(STATUS_ONLINE).equals(interfaceInfo.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "在线接口请先下线后再删除");
        }

        QueryWrapper<UserInterfaceInfo> quotaQueryWrapper = new QueryWrapper<>();
        quotaQueryWrapper.eq("interface_info_id", id);
        if (userInterfaceInfoService.count(quotaQueryWrapper) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已关联用户额度，不能直接删除");
        }

        QueryWrapper<InterfaceInvokeLog> invokeLogQueryWrapper = new QueryWrapper<>();
        invokeLogQueryWrapper.eq("interface_info_id", id);
        if (interfaceInvokeLogService.count(invokeLogQueryWrapper) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口已有调用记录，不能直接删除");
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateInterfaceStatus(Long id, int status) {
        InterfaceInfo interfaceInfo = this.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        if (interfaceInfo.getStatus() != null && interfaceInfo.getStatus() == status) {
            throw new BusinessException(
                    ErrorCode.OPERATION_ERROR,
                    status == STATUS_ONLINE ? "接口已是上线状态" : "接口已是下线状态");
        }
        if (status == STATUS_ONLINE) {
            validateOnlineInterface(interfaceInfo);
        }
        interfaceInfo.setStatus(status);
        return this.updateById(interfaceInfo);
    }

    private void validateOnlineInterface(InterfaceInfo interfaceInfo) {
        String method = interfaceInfo.getMethod();
        String url = interfaceInfo.getUrl();
        if (method == null || !SUPPORTED_METHODS.contains(method.toUpperCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口请求方法仅支持 GET 或 POST");
        }
        if (url == null || url.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址不能为空");
        }
        String path = normalizePath(url);
        if (path == null || path.isBlank() || !path.startsWith("/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址必须是有效路径");
        }
        QueryWrapper<InterfaceInfo> duplicateQueryWrapper = new QueryWrapper<>();
        duplicateQueryWrapper.eq("url", interfaceInfo.getUrl());
        duplicateQueryWrapper.eq("method", interfaceInfo.getMethod());
        duplicateQueryWrapper.ne("id", interfaceInfo.getId());
        if (this.count(duplicateQueryWrapper) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "相同请求路径和方法的接口已存在");
        }
    }

    private String normalizePath(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return URI.create(url).getPath();
        }
        return url;
    }
}
