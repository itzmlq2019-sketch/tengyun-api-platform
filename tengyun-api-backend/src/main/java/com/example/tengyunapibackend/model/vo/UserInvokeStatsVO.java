package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserInvokeStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long successInvokeCount;

    private Long failedInvokeCount;

    private Date lastInvokeTime;
}
