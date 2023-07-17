package com.authservice.jwtauth.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SigninDTO {
    @NotNull(message = "username is mandatory")
    private String usernameOrEmail;
    @NotNull(message = "password is mandatory")
    private String password;
}
