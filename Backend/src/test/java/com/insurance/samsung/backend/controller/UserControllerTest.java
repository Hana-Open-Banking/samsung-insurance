package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.UserRepository;
import com.insurance.samsung.backend.service.OAuthService;
import com.insurance.samsung.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthService oauthService;

    @Configuration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public OAuthService oauthService() {
            return Mockito.mock(OAuthService.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Test
    public void testGetCurrentUserWithValidToken() throws Exception {
        // Prepare mock data
        User user = User.builder()
                .userSeqNo("1000000001")
                .userName("홍길동")
                .gender('M')
                .phoneNumber("010-1234-5678")
                .userEmail("hong@test.com")
                .userInfo("19900101".toCharArray())
                .createdAt(LocalDateTime.now())
                .build();

        // Mock service responses
        when(oauthService.getUserByToken(anyString())).thenReturn(Optional.of(user));

        // Perform request
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer test-access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userSeqNo").value("1000000001"))
                .andExpect(jsonPath("$.userName").value("홍길동"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.userEmail").value("hong@test.com"))
                .andExpect(jsonPath("$.userInfo").value("19900101"));
    }

    @Test
    public void testGetCurrentUserWithInvalidToken() throws Exception {
        // Mock service response for invalid token
        when(oauthService.getUserByToken(anyString())).thenReturn(Optional.empty());
        when(oauthService.validateToken(anyString())).thenReturn(Optional.empty());

        // Perform request with invalid token
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.error_description").value("액세스 토큰이 유효하지 않습니다"));
    }

    @Test
    public void testGetCurrentUserWithMissingToken() throws Exception {
        // Perform request without token
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.error_description").value("액세스 토큰이 없습니다"));
    }

    @Test
    public void testGetCurrentUserWithValidTokenButNoUser() throws Exception {
        // Mock service responses
        when(oauthService.getUserByToken(anyString())).thenReturn(Optional.empty());
        when(oauthService.validateToken(anyString())).thenReturn(Optional.of(new OAuthToken()));

        // Perform request with token that has no associated user
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer test-access-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.error_description").value("토큰에 연결된 사용자가 없습니다"));
    }
}
