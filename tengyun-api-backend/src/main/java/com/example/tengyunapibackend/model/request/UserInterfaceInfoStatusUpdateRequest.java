package com.example.tengyunapibackend.model.request;

import lombok.Data;

@Data
public class UserInterfaceInfoStatusUpdateRequest {
    private Long userId;
    private Long interfaceInfoId;
    /**
     * 0-enabled, 1-disabled
     */
    private Integer status;
}
