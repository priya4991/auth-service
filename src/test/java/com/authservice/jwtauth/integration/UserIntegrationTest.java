package com.authservice.jwtauth.integration;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.authservice.jwtauth.model.DTO.SignupDTO;
import com.authservice.jwtauth.service.CrudUserService;

import jakarta.transaction.Transactional;

@ExtendWith( SpringExtension.class)
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
    private CrudUserService crudUserService;

    ///BeforeEach does not work with JUnit 4 @RunWith(SpringRunner.class)
    ///cannot use JUnit4 and JUnit 5 annotations together
    @BeforeEach   
    public void setup() {
        SignupDTO signupDTO = createSignupDTO();
        crudUserService.createUser(signupDTO);
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
    public void signin_success_validCreds() {
        
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
