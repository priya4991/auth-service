package com.authservice.jwtauth.model.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupDTO {
    @NotNull(message = "username is mandatory")
    private String username;
    @NotNull(message = "password is mandatory")
    private String password;

    @NotNull(message = "first name is mandatory")
    @JsonProperty("firstName")
    private String firstname;

    @NotNull(message = "last name is mandatory")
    @JsonProperty("lastName")
    private String lastname;

    @NotNull(message = "email id is mandatory")
    private String email;

    @NotNull(message = "phone number is mandatory")
    @JsonProperty("phoneNumber")
    private String phone;

    @NotNull(message = "date of birth is mandatory")
    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
