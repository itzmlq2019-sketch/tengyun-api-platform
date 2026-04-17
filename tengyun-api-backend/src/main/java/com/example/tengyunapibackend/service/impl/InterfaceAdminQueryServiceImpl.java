package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.mapper.InterfaceInfoMapper;
import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;
import com.example.tengyunapibackend.service.InterfaceAdminQueryService;
import com.example.tengyunapibackend.utils.TextFixUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterfaceAdminQueryServiceImpl implements InterfaceAdminQueryService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public List<InterfaceAdminVO> listInterfaces(
            String method,
            Integer status,
            String name,
            boolean includeSensitiveFields) {
        List<InterfaceAdminVO> result = interfaceInfoMapper.listAdminInterfaces(
                method, status, name, includeSensitiveFields, null, null);
        fixTextFields(result);
        return result;
    }

    @Override
    public Page<InterfaceAdminVO> pageInterfaces(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            boolean includeSensitiveFields) {
        long total = interfaceInfoMapper.countAdminInterfaces(method, status, name);
        Page<InterfaceAdminVO> page = new Page<>(pageNum, pageSize, total);
        if (total <= 0) {
            page.setRecords(List.of());
            return page;
        }
        long offset = (pageNum - 1) * pageSize;
        List<InterfaceAdminVO> records = interfaceInfoMapper.listAdminInterfaces(
                method, status, name, includeSensitiveFields, offset, pageSize);
        fixTextFields(records);
        page.setRecords(records);
        return page;
    }

    private void fixTextFields(List<InterfaceAdminVO> records) {
        for (InterfaceAdminVO vo : records) {
            vo.setName(TextFixUtils.fixMojibake(vo.getName()));
            vo.setDescription(TextFixUtils.fixMojibake(vo.getDescription()));
        }
    }
}
