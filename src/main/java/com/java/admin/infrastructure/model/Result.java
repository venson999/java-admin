package com.java.admin.infrastructure.model;

import com.java.admin.infrastructure.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return new Result<>("200", null, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>("200", null, data);
    }

    public static <T> Result<T> success(T data, String msg) {
        return new Result<>("200", msg, data);
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> error(String code, String msg) {
        return new Result<>(code, msg, null);
    }
}
