package com.authservice.jwtauth.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public User createUser(SignupDTO signup) throws Exception {
        if (userRepository.existsByUsername(signup.getUsername())) {
            throw new Exception("Username is already taken");
        }
        if (userRepository.existsByEmail((signup.getEmail()))) {
            throw new Exception("Email is already registered");
        }
        User user = new User();
        user.setUsername(signup.getUsername());
        user.setFirstName(signup.getFirstname());
        user.setLastName(signup.getLastname());
        user.setDateOfBirth(signup.getDob());
        user.setEmail(signup.getEmail());
        user.setPhoneNumber(signup.getPhone());
        user.setPassword(passwordEncoder.encode(signup.getPassword()));

        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);
        return user;

    }

    @Override
    public User updateUser(UpdateUserDTO updateUser, long id) throws Exception {
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
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO, long id) throws Exception {
        // try to find the user by id
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).get();
            // match old password with password in db
            if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())) {
                if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
                    user.setPassword(passwordEncoder.encode((changePasswordDTO.getNewPassword())));
                    userRepository.save(user);
                } else {
                    throw new Exception("New password cannot be same as old password");
                }
            } else {
                throw new Exception("Incorrect old password");
            }
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

}
