package com.linkhogar.application.user.changePassword;

public record ChangePasswordCommand(
        String email,
        String code,
        String newPassword
) {}