package com.example.tengyunapibackend.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class InterfaceInfoUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private String url;
    @JsonAlias("request_params")
    private String requestParams;
    @JsonAlias("request_header")
    private String requestHeader;
    @JsonAlias("response_header")
    private String responseHeader;
    private String method;
    private Integer status;
    @JsonAlias("user_id")
    private Long userId;
}
