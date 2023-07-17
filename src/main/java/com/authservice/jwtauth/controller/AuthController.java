package com.authservice.jwtauth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.jwtauth.config.TokenManager;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.Auth.TokenResponse;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SigninDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;
import com.authservice.jwtauth.service.CrudUserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private CrudUserService crudUserService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody SigninDTO login) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    login.getUsernameOrEmail(), login.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenManager.generateJwtToken(authentication);

            return new ResponseEntity<TokenResponse>(new TokenResponse(token), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupDTO signup) {
        try {

        User user = crudUserService.createUser(signup);

        return new ResponseEntity<User>(user, HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/changepassword/{id}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO,
            @PathVariable(name = "id") long id) {
        try {
            crudUserService.changePassword(changePasswordDTO, id);
            return new ResponseEntity<String>("Password change successfully", HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/updateuserdetails/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateUserDTO, @PathVariable(name = "id") long id) {
        try {
            User user = crudUserService.updateUser(updateUserDTO, id);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = crudUserService.getAllUsers();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

}
