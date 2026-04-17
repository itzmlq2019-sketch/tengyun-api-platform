package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class InterfaceAdminSafeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String url;
    private String method;
    private Integer status;

    private Long boundUserCount;
    private Long totalLeftNum;
    private Long totalInvokeNum;
    private Long successInvokeCount;
    private Long failedInvokeCount;
    private Date lastInvokeTime;

    private Date createTime;
    private Date updateTime;
}
