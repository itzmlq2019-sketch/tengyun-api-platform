package com.example.tengyunapibackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserCredentialResetResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String accessKey;

    private String secretKey;
}
