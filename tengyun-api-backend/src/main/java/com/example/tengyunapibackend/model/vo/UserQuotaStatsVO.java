package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserQuotaStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long boundInterfaceCount;

    private Long totalLeftNum;

    private Long totalInvokeNum;
}
