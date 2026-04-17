package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserAdminVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userAccount;

    private String userName;

    private String userAvatar;

    private String userRole;

    private String accessKey;

    private String secretKeyMasked;

    private Long boundInterfaceCount;

    private Long totalLeftNum;

    private Long totalInvokeNum;

    private Long successInvokeCount;

    private Long failedInvokeCount;

    private Date lastInvokeTime;

    private Date createTime;

    private Date updateTime;
}
