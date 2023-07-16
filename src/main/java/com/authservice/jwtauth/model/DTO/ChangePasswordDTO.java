package com.authservice.jwtauth.model.DTO;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
