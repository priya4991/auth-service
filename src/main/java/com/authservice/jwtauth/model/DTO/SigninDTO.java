package com.authservice.jwtauth.model.DTO;

import lombok.Data;

@Data
public class SigninDTO {
    private String usernameOrEmail;
    private String password;
}
