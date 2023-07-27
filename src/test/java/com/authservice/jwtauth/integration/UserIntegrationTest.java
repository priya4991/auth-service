package com.authservice.jwtauth.integration;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.test.context.support.WithMockUser;
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

    private long id;

    /// BeforeEach does not work with JUnit 4 @RunWith(SpringRunner.class)
    /// cannot use JUnit4 and JUnit 5 annotations together
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

        id = userRepository.findAll().get(0).getId();
    }

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void signup_success_when_validRequest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signuprequest.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("username").value("priya"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("pdu@gmail.com"));
    }

    @Test
    public void signup_badRequest_when_invalidDate() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signuprequest_invalidDate.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Invalid date format"));
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
    public void signin_success_when_validCreds() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signinrequest.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void signin_badRequest_when_invalidCreds() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:signinrequest_invalid.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser
    public void changePassword_success_when_validRequest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:changePassword.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/changepassword/{id}", id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Password changed successfully"));
    }

    @Test
    @WithMockUser
    public void changePassword_badRequest_when_newOldPasswordSame() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:changePassword_newOldSame.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/changepassword/{id}", id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("error").value("New password cannot be same as old password"));
    }

    @Test
    @WithMockUser
    public void changePassword_badRequest_when_incorrectOldPassword() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:changePassword_incorrectOld.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/changepassword/{id}", id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Incorrect old password"));
    }

    @Test
    @WithMockUser
    public void changePassword_badRequest_when_invalidUser() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:changePassword.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/changepassword/123")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("User not found"));
    }

    @Test
    @WithMockUser
    public void updateUser_success() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:updateUser.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/updateuserdetails/{id}", id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("dateOfBirth").value("1999-06-06"))
                .andExpect(MockMvcResultMatchers.jsonPath("lastName").value("lawy"));
    }

    @Test
    @WithMockUser
    public void updateUser_badRequest_when_invalidUser() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:updateUser.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/updateuserdetails/123")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("User not found"));
    }

    @Test
    @WithMockUser
    public void updateUser_badRequest_when_invalidDate() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:updateUser_invalidDate.json");
        String content = new String(resource.getInputStream().readAllBytes());
        mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/updateuserdetails/{id}", id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("Invalid date format"));
    }

    @Test
    @WithMockUser
    public void deletUser_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/auth/delete/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User is deleted"));
    }

    @Test
    @WithMockUser
    public void deletUser_badRequest_when_invalidUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/auth/delete/123"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value("User not found"));
    }

    @Test
    @WithMockUser
    public void deletAllUsers_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/auth/deleteall"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("All users deleted"));
    }

    @Test
    @WithMockUser
    public void getAllUsers_success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/getall"))
                .andExpect(MockMvcResultMatchers.status().isOk());
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
