package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class InterfaceInvokeStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long interfaceInfoId;

    private String interfaceName;

    private Long totalInvokeCount;

    private Long successInvokeCount;

    private Long failedInvokeCount;

    private Date lastInvokeTime;
}
