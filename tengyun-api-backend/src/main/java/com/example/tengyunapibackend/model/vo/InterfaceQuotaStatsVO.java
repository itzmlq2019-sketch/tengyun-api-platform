package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class InterfaceQuotaStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long interfaceInfoId;

    private Long boundUserCount;

    private Long totalLeftNum;

    private Long totalInvokeNum;
}
