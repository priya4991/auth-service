package com.authservice.jwtauth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.jwtauth.config.security.TokenManager;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.Auth.TokenResponse;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SigninDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;
import com.authservice.jwtauth.service.CrudUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CrudUserService crudUserService;

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> authenticateUser(@RequestBody SigninDTO login) {
            String token = crudUserService.signinUser(login.getUsernameOrEmail(), login.getPassword());
            return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody SignupDTO signup) {
        String token = crudUserService.createUser(signup);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/changepassword/{id}")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            @PathVariable(name = "id") long id) {
        crudUserService.changePassword(changePasswordDTO, id);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @PutMapping("/updateuserdetails/{id}")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserDTO updateUserDTO, @PathVariable(name = "id") long id) {
        User user = crudUserService.updateUser(updateUserDTO, id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = crudUserService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(name = "id") long id) {
        crudUserService.deleteUser(id);
        return new ResponseEntity<>("User is deleted", HttpStatus.OK);
    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<String> deleteAll() {
        crudUserService.deleteAll();
        return new ResponseEntity<>("All users deleted", HttpStatus.OK);
    }

}
