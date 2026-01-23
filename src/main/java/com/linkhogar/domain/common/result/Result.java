package com.linkhogar.domain.common.result;

import lombok.Getter;


public class Result<T> {
    @Getter
    private final T value;
    @Getter
    private final Error error;
    @Getter
    private final boolean isSuccess;

    private Result(T value, Error error, boolean isSuccess){
        // Validaciones defensivas
        if (isSuccess && error != Error.NONE) {
            throw new IllegalArgumentException("Un resultado exitoso no puede tener un error");
        }
        if (!isSuccess && error == Error.NONE) {
            throw new IllegalArgumentException("Un resultado fallido debe tener un error");
        }

        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T> Result<T> success(T value){ return new Result<>(value, Error.NONE, true);}
    public static <T> Result<T> failure(Error error){return new Result<>(null, error, false);}
}
