package com.example.tengyunapibackend.service.support;

import java.util.Date;

public interface RequestValidationService {

    void requirePositiveId(Long value, String fieldName);

    void requirePositiveNumber(Integer value, String fieldName);

    void requirePage(long pageNum, long pageSize, long maxPageSize);

    void requireTimeRange(Date startTime, Date endTime, String message);
}
