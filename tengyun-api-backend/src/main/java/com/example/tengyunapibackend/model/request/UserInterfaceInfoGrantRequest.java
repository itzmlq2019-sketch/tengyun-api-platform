package com.example.tengyunapibackend.model.request;

import lombok.Data;

@Data
public class UserInterfaceInfoGrantRequest {
    private Long userId;
    private Long interfaceInfoId;
    private Integer grantNum;
    private String description;
}
