package com.authservice.jwtauth.service;

import java.util.List;

import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;

public interface CrudUserService {
    public User createUser(SignupDTO signup) throws Exception;
    public User updateUser(UpdateUserDTO updateUser, long id) throws Exception;
    public void changePassword(ChangePasswordDTO changePassword, long id) throws Exception;
    public List<User> getAllUsers();
}
