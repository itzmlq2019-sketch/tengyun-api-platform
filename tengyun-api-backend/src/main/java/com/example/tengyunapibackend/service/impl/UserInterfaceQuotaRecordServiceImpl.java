package com.example.tengyunapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tengyunapibackend.mapper.UserInterfaceQuotaRecordMapper;
import com.example.tengyunapibackend.service.UserInterfaceQuotaRecordService;
import com.example.tengyunapicommon.entity.UserInterfaceQuotaRecord;
import org.springframework.stereotype.Service;

@Service
public class UserInterfaceQuotaRecordServiceImpl extends ServiceImpl<UserInterfaceQuotaRecordMapper, UserInterfaceQuotaRecord>
        implements UserInterfaceQuotaRecordService {
}
