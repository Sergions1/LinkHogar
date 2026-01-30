package com.linkhogar.application.user.login;

public record UserLoginCommand (
    String mail,
    String password
){}
