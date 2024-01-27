package com.authservice.jwtauth.config.exception;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorResponseDTO<T> implements Serializable {
    private String result = "failure";
    private T error;

    public ErrorResponseDTO(T body) {
        this.error = body;
    }
}
