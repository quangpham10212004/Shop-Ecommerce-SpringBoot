package com.aiecommerce.product.dto;

import lombok.*;

@Builder
public record BaseResponse <T>(
    Boolean  success,
    T data,
    String message,
    String erorCode
){
    public static <T> BaseResponse<T> ok(T data){
        return BaseResponse.<T>builder().success(true).data(data).build();

    }
    public static <T> BaseResponse<T> ok(T data, String message) {
        return BaseResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    public static <T> BaseResponse<T> success(T data) {
        return ok(data);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return ok(data, message);
    }

    public static <T> BaseResponse<T> error(String errorCode, String message) {
        return BaseResponse.<T>builder()
                .success(false)
                .erorCode(errorCode)
                .message(message)
                .build();
    }


}

