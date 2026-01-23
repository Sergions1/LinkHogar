package com.linkhogar.domain.common.result;

public record Error(String code, String message, ErrorType type) {
    public static final Error NONE = new Error("", "", ErrorType.FAILURE);
    public static final Error NULL_VALUE = new Error("Error.NullValue", "The result value is null", ErrorType.FAILURE);

    public static Error failure(String code, String message) {
        return new Error(code, message, ErrorType.FAILURE);
    }

    public static Error notFound(String code, String message) {
        return new Error(code, message, ErrorType.NOT_FOUND);
    }

    public static Error conflict(String code, String message) {
        return new Error(code, message, ErrorType.CONFLICT);
    }

    public static Error validation(String code, String message) {
        return new Error(code, message, ErrorType.VALIDATION);
    }

    public static Error unauthorized(String code, String message) {
        return new Error(code, message, ErrorType.UNAUTHORIZED);
    }
}