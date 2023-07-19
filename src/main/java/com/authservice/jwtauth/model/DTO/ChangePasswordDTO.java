package com.authservice.jwtauth.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDTO {
    @NotNull(message = "Old password is mandatory")
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;
    
    @NotBlank(message = "New password cannot be blank")
    @NotNull(message = "New password is mandatory")
    private String newPassword;
}
