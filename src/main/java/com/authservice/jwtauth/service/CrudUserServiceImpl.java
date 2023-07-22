package com.authservice.jwtauth.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.authservice.jwtauth.config.exception.BadRequestException;
import com.authservice.jwtauth.model.Role;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;
import com.authservice.jwtauth.repository.RoleRepository;
import com.authservice.jwtauth.repository.UserRepository;

@Service
public class CrudUserServiceImpl implements CrudUserService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(SignupDTO signup) throws BadRequestException {
        if (userRepository.existsByUsername(signup.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail((signup.getEmail()))) {
            throw new BadRequestException("Email is already registered");
        }
        User user = new User();
        user.setUsername(signup.getUsername());
        user.setFirstName(signup.getFirstname());
        user.setLastName(signup.getLastname());
        user.setDateOfBirth(signup.getDob());
        user.setEmail(signup.getEmail());
        user.setPhoneNumber(signup.getPhone());
        user.setPassword(passwordEncoder.encode(signup.getPassword()));


        Role roles = roleRepository.findByName("ROLE_USER").orElse(new Role("ROLE_USER"));
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);
        return user;

    }

    @Override
    public User updateUser(UpdateUserDTO updateUser, long id) throws BadRequestException {
        User user = null;
        if (userRepository.existsById(id)) {
            user = userRepository.findById(id).get();
            if (updateUser.getDob() != null) {
                user.setDateOfBirth(updateUser.getDob());
            }
            if (updateUser.getFirstname() != null) {
                user.setFirstName(updateUser.getFirstname());
            }
            if (updateUser.getLastname() != null) {
                user.setLastName(updateUser.getLastname());
            }
            if (updateUser.getPhone() != null) {
                user.setPhoneNumber(updateUser.getPhone());
            }
            userRepository.save(user);
        } else {
            throw new BadRequestException("User not found");
        }
        return user;
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, long id) throws BadRequestException {
        // try to find the user by id
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();
            // match old password with password in db
            if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
                if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
                    user.setPassword(passwordEncoder.encode((changePasswordDTO.getNewPassword())));
                    userRepository.save(user);
                } else {
                    throw new BadRequestException("New password cannot be same as old password");
                }
            } else {
                throw new BadRequestException("Incorrect old password");
            }
        } else {
            throw new BadRequestException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public void deleteUser(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new BadRequestException("User not found");
        }
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

}
