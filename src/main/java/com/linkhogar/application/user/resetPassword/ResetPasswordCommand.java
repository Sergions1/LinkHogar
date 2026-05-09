package com.linkhogar.application.user.resetPassword;

public record ResetPasswordCommand(String mail, String code, String newPassword) {}
