package com.aiecommerce.user.common;


public class BaseResponse<T> {
    private T data;
    private String message;
    public BaseResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public String getMessage() {
        return message;
    }
}
