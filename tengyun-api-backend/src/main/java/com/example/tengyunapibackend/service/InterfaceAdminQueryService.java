package com.example.tengyunapibackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tengyunapibackend.model.vo.InterfaceAdminVO;

import java.util.List;

public interface InterfaceAdminQueryService {

    List<InterfaceAdminVO> listInterfaces(String method, Integer status, String name, boolean includeSensitiveFields);

    Page<InterfaceAdminVO> pageInterfaces(
            String method,
            Integer status,
            String name,
            long pageNum,
            long pageSize,
            boolean includeSensitiveFields);
}
