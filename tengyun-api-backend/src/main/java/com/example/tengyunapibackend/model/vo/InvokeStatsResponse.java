package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class InvokeStatsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long totalInvokeCount;

    private Long successInvokeCount;

    private Long failedInvokeCount;

    private List<InterfaceInvokeStatsVO> interfaceStats;
}
