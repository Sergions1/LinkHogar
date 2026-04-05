package com.linkhogar.application.user.changePassword;

public record ChangePasswordRequest(
        String mail,
        String code,
        String newPassword
) {}