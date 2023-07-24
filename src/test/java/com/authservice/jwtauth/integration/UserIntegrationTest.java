package com.authservice.jwtauth.integration;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.authservice.jwtauth.model.Role;
import com.authservice.jwtauth.model.User;
import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc; 
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    private String BEARERTOKEN;

    ///BeforeEach does not work with JUnit 4 @RunWith(SpringRunner.class)
    ///cannot use JUnit4 and JUnit 5 annotations together
    @BeforeEach   
    public void setup() {
        SignupDTO signup = createSignupDTO();
        User user = new User();
        user.setUsername(signup.getUsername());
        user.setFirstName(signup.getFirstname());
        user.setLastName(signup.getLastname());
        user.setDateOfBirth(signup.getDob());
        user.setEmail(signup.getEmail());
        user.setPhoneNumber(signup.getPhone());
        user.setPassword(passwordEncoder.encode(signup.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_USER"));
        user.setRoles(roles);
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        final long expirationMillis = System.currentTimeMillis() + (5 * 60 * 1000);
        BEARERTOKEN = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

    }

    @Test
    public void signup_success_when_validRequest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signuprequest.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("priya"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("pdu@gmail.com"));
    }

    @Test
    public void signup_badRequest_when_duplicateUsername() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signuprequest_duplicateUsername.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Username is already taken"));
    }

    @Test
    public void signup_badRequest_when_duplicateEmail() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signuprequest_duplicateEmail.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Email is already registered"));
    }

    @Test
    public void signin_success_validCreds() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signinrequest.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void signin_badRequest_invalidCreds() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signinrequest_invalid.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void changePassword_success_validRequest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:changePassword.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/changepassword/1")
        .content(content)
        .header("Authorization", "Bearer " + BEARERTOKEN)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Password changed successfully"));
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

}
