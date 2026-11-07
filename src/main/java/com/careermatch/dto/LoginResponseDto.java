package com.careermatch.dto;

public class LoginResponseDto {

    private String token;
    private String message;
    private String email;

    public LoginResponseDto(String token, String message, String email) {
        this.token = token;
        this.message = message;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }
}