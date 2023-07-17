package com.authservice.jwtauth.model.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupDTO {
    @NotNull(message = "Username is mandatory")
    private String username;
    @NotNull(message = "Password is mandatory")
    private String password;

    @NotNull(message = "First name is mandatory")
    @JsonProperty("firstName")
    private String firstname;

    @NotNull(message = "Last name is mandatory")
    @JsonProperty("lastName")
    private String lastname;

    @NotNull(message = "Email ID is mandatory")
    @Email
    private String email;

    @NotNull(message = "Phone number is mandatory")
    @JsonProperty("phoneNumber")
    private String phone;

    @NotNull(message = "Date of birth is mandatory")
    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;
}
