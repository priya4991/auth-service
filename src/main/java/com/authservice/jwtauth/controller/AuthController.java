package com.authservice.jwtauth.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.jwtauth.config.TokenManager;
import com.authservice.jwtauth.model.Role;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.Auth.TokenResponse;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.LoginDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.repository.RoleRepository;
import com.authservice.jwtauth.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenManager tokenManager;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO login) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login.getUsernameOrEmail(), login.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenManager.generateJwtToken(authentication);

            return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupDTO signup) {
        if (userRepository.existsByUsername(signup.getUsername())) {
            return new ResponseEntity<>("Username is already taken", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail((signup.getEmail()))) {
            return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/changepassword/{id}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
            @PathVariable(name = "id") long id) {
        try {
            // try to find the user by id
            if (userRepository.existsById(id)) {
                User user = userRepository.findById(id).get();
                // match old password with password in db
                if (passwordEncoder.matches(changePasswordDTO.getOldPassword(), user.getPassword())
                        && !changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
                    user.setPassword(passwordEncoder.encode((changePasswordDTO.getNewPassword())));
                    userRepository.save(user);
                    return new ResponseEntity<String>("Password changed successfully", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("Incorrect password combination", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>("User not found", HttpStatus.NOT_FOUND);
            }

        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/getall")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

}
