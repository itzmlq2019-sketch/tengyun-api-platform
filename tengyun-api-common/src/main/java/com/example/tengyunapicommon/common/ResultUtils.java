package com.example.tengyunapicommon.common;

import com.example.tengyunapicommon.exception.ErrorCode;

/**
 * 返回工具类
 */
public class ResultUtils {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, message);
    }

    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage());
    }

    public static BaseResponse<?> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    public static BaseResponse<?> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}