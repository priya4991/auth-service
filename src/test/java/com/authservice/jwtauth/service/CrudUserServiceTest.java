package com.authservice.jwtauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.authservice.jwtauth.config.exception.BadRequestException;
import com.authservice.jwtauth.model.Role;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.ChangePasswordDTO;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.model.DTO.UpdateUserDTO;
import com.authservice.jwtauth.repository.RoleRepository;
import com.authservice.jwtauth.repository.UserRepository;

public class CrudUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private CrudUserServiceImpl crudUserService;

    private long userId1 = 1;
    private long userId2 = 2;
    private String username = "priya";
    private String email = "priya@gmail.com";

    public CrudUserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Before
    public void setup() {
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(userRepository.findById(userId1)).thenReturn(Optional.of(createUser()));

        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        when(passwordEncoder.encode(anyString())).thenReturn("xyz123456");
    }

    @Test
    public void createUser_return_user_when_validRequest() {
        SignupDTO signup = createSignupDTO();
        User user = crudUserService.createUser(signup);
        assertEquals("fiona", user.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUser_return_BadRequest_when_existingUsername() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            SignupDTO signup = createSignupDTO();
            signup.setUsername(username);
            crudUserService.createUser(signup);
        });
        assertEquals("Username is already taken", ex.getMessage());

    }

    @Test
    public void createUser_return_BadRequest_when_existingEmail() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            SignupDTO signup = createSignupDTO();
            signup.setEmail(email);
            crudUserService.createUser(signup);
        });
        assertEquals("Email is already registered", ex.getMessage());
    }

    @Test
    public void updateUser_return_success_when_validRequest() {
        UpdateUserDTO updateUserDTO = createUpdateDTO();
        User user = crudUserService.updateUser(updateUserDTO, userId1);
        assertEquals("ff", user.getFirstName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void updateUser_return_BadRequest_when_invalidUser() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            UpdateUserDTO updateUserDTO = createUpdateDTO();
            crudUserService.updateUser(updateUserDTO, userId2);
        });
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void updateUser_return_BadRequest_when_invalidDateFormat() {
        assertThrows(DateTimeParseException.class, () -> {
            UpdateUserDTO updateUserDTO = createUpdateDTO();
            updateUserDTO.setDob(LocalDate.parse("199-06-06"));
            crudUserService.updateUser(updateUserDTO, userId2);
        });
    }

    @Test
    public void changePassword_return_success() {
        ChangePasswordDTO changePasswordDTO = changePasswordDTO();
        when(passwordEncoder.matches(eq(changePasswordDTO.getOldPassword()), any())).thenReturn(true);
        crudUserService.changePassword(changePasswordDTO, userId1);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void changePassword_return_BadRequest_incorrectOldPassword() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            ChangePasswordDTO changePasswordDTO = changePasswordDTO();
            when(passwordEncoder.matches(eq(changePasswordDTO.getOldPassword()), any())).thenReturn(false);
            crudUserService.changePassword(changePasswordDTO, userId1);
        });
        assertEquals("Incorrect old password", ex.getMessage());
    }

    @Test
    public void changePassword_return_BadRequest_sameOldNewPassword() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            ChangePasswordDTO changePasswordDTO = changePasswordDTO();
            changePasswordDTO.setNewPassword("abc");
            when(passwordEncoder.matches(eq(changePasswordDTO.getOldPassword()), any())).thenReturn(true);
            crudUserService.changePassword(changePasswordDTO, userId1);
        });
        assertEquals("New password cannot be same as old password", ex.getMessage());
    }

    @Test
    public void getAll_return_success() {
        crudUserService.getAllUsers();
        verify(userRepository).findAll();
    }

    @Test
    public void deleteById_return_success() {
        crudUserService.deleteUser(userId1);
        verify(userRepository).deleteById(userId1);
    }

    @Test
    public void deleteById_return_BadRequest_invalidUserId() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            crudUserService.deleteUser(userId2);
        });
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void deleteAll_success() {
        crudUserService.deleteAll();
        verify(userRepository).deleteAll();
    }

   

    private SignupDTO createSignupDTO() {
        return SignupDTO.builder()
                .dob(LocalDate.parse("1994-07-03"))
                .firstname("fiona")
                .lastname("dutta")
                .username("pdutta")
                .email("pdutta@gmail.com")
                .password("abc123")
                .phone("+2345677889")
                .build();
    }

    private UpdateUserDTO createUpdateDTO() {
        return UpdateUserDTO.builder()
                .firstname("ff")
                .build();
    }

    private ChangePasswordDTO changePasswordDTO() {
        return ChangePasswordDTO.builder()
                .oldPassword("abc")
                .newPassword("abc123")
                .build();
    }

    private User createUser() {
        User user = new User();
        return user;
    }

}
