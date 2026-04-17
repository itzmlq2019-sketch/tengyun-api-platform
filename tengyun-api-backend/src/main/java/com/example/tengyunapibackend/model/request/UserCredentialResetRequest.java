package com.example.tengyunapibackend.model.request;

import lombok.Data;

@Data
public class UserCredentialResetRequest {
    private Long userId;
    private Boolean resetAccessKey;
    private Boolean resetSecretKey;
}
