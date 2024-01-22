package com.authservice.jwtauth.service;

import java.util.List;

import com.authservice.jwtauth.config.exception.BadRequestException;
import com.authservice.jwtauth.model.DTO.SigninDTO;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;

public interface CrudUserService {
    String createUser(SignupDTO signup) throws BadRequestException;
    String signinUser(String usernameOrEmail, String password) throws BadRequestException;
    User updateUser(UpdateUserDTO updateUser, long id) throws BadRequestException;
    void changePassword(ChangePasswordDTO changePassword, long id) throws BadRequestException;
    List<User> getAllUsers();
    void deleteUser(long id);
    void deleteAll();
}
