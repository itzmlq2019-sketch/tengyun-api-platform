package com.example.tengyunapibackend.model.request;

import lombok.Data;

@Data
public class UserInterfaceInfoRevokeRequest {
    private Long userId;
    private Long interfaceInfoId;
    private Integer revokeNum;
    private String description;
}
