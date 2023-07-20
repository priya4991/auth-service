package com.authservice.jwtauth.service;

import java.util.List;

import com.authservice.jwtauth.config.exception.BadRequestException;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;

public interface CrudUserService {
    public User createUser(SignupDTO signup) throws BadRequestException;
    public User updateUser(UpdateUserDTO updateUser, long id) throws BadRequestException;
    public void changePassword(ChangePasswordDTO changePassword, long id) throws BadRequestException;
    public List<User> getAllUsers();
    public void deleteUser(long id);
    public void deleteAll();
}
