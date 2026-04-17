package com.example.tengyunapibackend.service.support.impl;

import com.example.tengyunapibackend.service.support.RequestValidationService;
import com.example.tengyunapicommon.exception.BusinessException;
import com.example.tengyunapicommon.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestValidationServiceImpl implements RequestValidationService {

    @Override
    public void requirePositiveId(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " must be positive");
        }
    }

    @Override
    public void requirePositiveNumber(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " must be positive");
        }
    }

    @Override
    public void requirePage(long pageNum, long pageSize, long maxPageSize) {
        if (pageNum <= 0 || pageSize <= 0 || pageSize > maxPageSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "invalid pageNum/pageSize");
        }
    }

    @Override
    public void requireTimeRange(Date startTime, Date endTime, String message) {
        if (startTime != null && endTime != null && startTime.after(endTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
    }
}
