package com.authservice.jwtauth.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SigninDTO {
    @NotNull(message = "username is mandatory")
    private String usernameOrEmail;
    @NotNull(message = "password is mandatory")
    private String password;
}
