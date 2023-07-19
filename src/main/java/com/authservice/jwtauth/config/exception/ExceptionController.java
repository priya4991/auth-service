package com.authservice.jwtauth.config.exception;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, List<String>>> badException(BadRequestException ex) {
        return new ResponseEntity<Map<String, List<String>>>(getErrorsMap(List.of(ex.getMessage())),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> invalidArgumentException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<Map<String, List<String>>>(getErrorsMap(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, List<String>>> badCredentialException(BadCredentialsException ex) {
        return new ResponseEntity<Map<String, List<String>>>(getErrorsMap(List.of(ex.getMessage())),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DateTimeParseException.class) 
    public ResponseEntity<Map<String, List<String>>> deserializationException(DateTimeParseException ex) {
        return new ResponseEntity<Map<String, List<String>>>(getErrorsMap(List.of("Invalid date format")),
                HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errorList) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("error", errorList);
        return errorResponse;
    }
}
