package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorizationSummaryVO implements Serializable {

    private Long totalBindingCount;

    private Long enabledBindingCount;

    private Long disabledBindingCount;

    private Long totalLeftNum;

    private Long totalInvokeNum;

    private Long affectedUserCount;

    private Long affectedInterfaceCount;

    private static final long serialVersionUID = 1L;
}
