package com.example.tengyunapibackend.model.request;

import lombok.Data;

@Data
public class UserRoleUpdateRequest {
    private Long userId;
    private String userRole;
}
