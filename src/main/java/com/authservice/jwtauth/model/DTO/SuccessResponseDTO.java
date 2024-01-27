package com.authservice.jwtauth.model.DTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class SuccessResponseDTO<T> implements Serializable {
    private String result = "success";
    private T body;


    public SuccessResponseDTO(T body) {
        this.body = body;
    }
}
