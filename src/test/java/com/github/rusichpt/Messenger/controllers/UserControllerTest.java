package com.github.rusichpt.Messenger.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rusichpt.Messenger.dto.AuthRequest;
import com.github.rusichpt.Messenger.dto.AuthResponse;
import com.github.rusichpt.Messenger.dto.UserUpdateDTO;
import com.github.rusichpt.Messenger.dto.UserUpdatePassDTO;
import com.github.rusichpt.Messenger.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    private final ObjectMapper objectMapper;
    @MockBean // заменяем реальный сервис, чтобы не спамить почту на несуществующий адрес.
    private final EmailService emailService;
    private final MockMvc mockMvc;

    @Autowired
    public UserControllerTest(ObjectMapper objectMapper, EmailService emailService, MockMvc mockMvc) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
        this.mockMvc = mockMvc;
    }

    private HttpHeaders getAuthHeader() throws Exception {
        AuthRequest request = new AuthRequest("user1", "123");
        String contentAsString = mockMvc.perform(post("/api/v1/auth/signin")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthResponse response = objectMapper.readValue(new ByteArrayInputStream(contentAsString.getBytes()), AuthResponse.class);
        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(response.getToken());
        return header;
    }

    @Test
    void getProfile() throws Exception {
        mockMvc.perform(get("/api/v1/users/profile")
                        .headers(getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void updateUserProfile() throws Exception {
        UserUpdateDTO profile = new UserUpdateDTO("username", "test@mail.ru", "Name", "Surname");
        mockMvc.perform(put("/api/v1/users/profile")
                        .content(objectMapper.writeValueAsString(profile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(profile.getUsername()));
    }

    @Test
    void updateUserProfileNotUniqueUsername() throws Exception {
        UserUpdateDTO profile = new UserUpdateDTO("user1", "test@mail.ru", "Name", "Surname");
        mockMvc.perform(put("/api/v1/users/profile")
                        .content(objectMapper.writeValueAsString(profile))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getAuthHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserBlankPass() throws Exception {
        UserUpdatePassDTO request = new UserUpdatePassDTO("");
        mockMvc.perform(patch("/api/v1/users/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getAuthHeader()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/delete")
                        .headers(getAuthHeader()))
                .andExpect(status().isNoContent());
    }

    @Test
    void sendEmailForConfirmation() throws Exception {
        mockMvc.perform(get("/api/v1/users/send-email")
                        .headers(getAuthHeader()))
                .andExpect(status().isOk());
        verify(emailService, times(1)).sendConfirmationCode(any());
    }

}