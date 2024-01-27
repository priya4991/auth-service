package com.authservice.jwtauth.controller;

import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SigninDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.SuccessResponseDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.service.CrudUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000/", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CrudUserService crudUserService;

    @PostMapping("/signin")
    public ResponseEntity<SuccessResponseDTO<String>> authenticateUser(@RequestBody SigninDTO login) {
            String token = crudUserService.signinUser(login.getUsernameOrEmail(), login.getPassword());
            return new ResponseEntity<>(createSuccessResponse(token), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponseDTO<String>> registerUser(@RequestBody SignupDTO signup) {
        String token = crudUserService.createUser(signup);
        return new ResponseEntity<>(createSuccessResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/changepassword/{id}")
    public ResponseEntity<SuccessResponseDTO<String>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO,
                                                                     @PathVariable(name = "id") long id) {
        crudUserService.changePassword(changePasswordDTO, id);
        return new ResponseEntity<>(createSuccessResponse("Password changed successfully"), HttpStatus.OK);
    }

    @PutMapping("/updateuserdetails/{id}")
    public ResponseEntity<SuccessResponseDTO<User>> updateUser(@RequestBody UpdateUserDTO updateUserDTO, @PathVariable(name = "id") long id) {
        User user = crudUserService.updateUser(updateUserDTO, id);
        return new ResponseEntity<>(createSuccessResponse(user), HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity<SuccessResponseDTO<List<User>>> getAllUsers() {
        List<User> users = crudUserService.getAllUsers();
        return new ResponseEntity<>(createSuccessResponse(users), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponseDTO<String>> deleteUser(@PathVariable(name = "id") long id) {
        crudUserService.deleteUser(id);
        return new ResponseEntity<>(createSuccessResponse("User is deleted"), HttpStatus.OK);
    }

    @DeleteMapping("/deleteall")
    public ResponseEntity<SuccessResponseDTO<String>> deleteAll() {
        crudUserService.deleteAll();
        return new ResponseEntity<>(createSuccessResponse("All users deleted"), HttpStatus.OK);
    }

    private <T> SuccessResponseDTO<T> createSuccessResponse(T response) {
        return new SuccessResponseDTO<>(response);
    }

}
