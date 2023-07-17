package com.authservice.jwtauth.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotNull(message = "old password is mandatory")
    private String oldPassword;
    @NotNull(message = "new password is mandatory")
    private String newPassword;
}
