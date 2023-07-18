package com.authservice.jwtauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
import com.authservice.jwtauth.model.DTO.SignupDTO;
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
        assertEquals(user.getFirstName(), "fiona");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUser_return_BadRequest_when_existingUsername() {
        assertThrows(BadRequestException.class, () -> {
            SignupDTO signup = createSignupDTO();
            signup.setUsername(username);
            crudUserService.createUser(signup);
        });

    }

    @Test
    public void createUser_return_BadRequest_when_existingEmail() {
        assertThrows(BadRequestException.class, () -> {
            SignupDTO signup = createSignupDTO();
            signup.setEmail(email);
            crudUserService.createUser(signup);
        });

    }

    private SignupDTO createSignupDTO() {
        SignupDTO signup = SignupDTO.builder()
                .dob(LocalDate.parse("1994-07-03"))
                .firstname("fiona")
                .lastname("dutta")
                .username("pdutta")
                .email("pdutta@gmail.com")
                .password("abc123")
                .phone("+2345677889")
                .build();
        return signup;
    }

}
