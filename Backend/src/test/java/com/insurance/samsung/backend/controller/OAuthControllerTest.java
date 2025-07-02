package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.entity.OAuthClient;
import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.repository.OAuthClientRepository;
import com.insurance.samsung.backend.repository.OAuthTokenRepository;
import com.insurance.samsung.backend.service.OAuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OAuthController.class)
@Import(OAuthControllerTest.TestConfig.class)
public class OAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthService oauthService;

    @Configuration
    static class TestConfig {
        @Bean
        public OAuthService oauthService() {
            return Mockito.mock(OAuthService.class);
        }

        @Bean
        public OAuthClientRepository oauthClientRepository() {
            return Mockito.mock(OAuthClientRepository.class);
        }

        @Bean
        public OAuthTokenRepository oauthTokenRepository() {
            return Mockito.mock(OAuthTokenRepository.class);
        }
    }

    @Test
    public void testTokenEndpointWithValidAuthorization() throws Exception {
        // Prepare mock data
        OAuthClient client = new OAuthClient();
        client.setClientId("CLIENT001");
        client.setClientSecret("secret123");
        client.setClientName("Test Client");

        OAuthToken token = new OAuthToken();
        token.setAccessTokenId("test-access-token");
        token.setRefreshToken("test-refresh-token");
        token.setExpiresIn(3600);
        token.setScope("read");
        token.setIssuedAt(LocalDateTime.now());
        token.setClient(client);

        // Mock service response
        when(oauthService.issueToken(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(token));

        // Perform request with Authorization header
        mockMvc.perform(post("/oauth/token")
                        .param("grant_type", "client_credentials")
                        .param("scope", "read")
                        .header("Authorization", "Basic Q0xJRU5UMDAxOnNlY3JldDEyMw==") // CLIENT001:secret123 in Base64
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("test-access-token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600))
                .andExpect(jsonPath("$.refresh_token").value("test-refresh-token"))
                .andExpect(jsonPath("$.scope").value("read"));
    }

    @Test
    public void testTokenEndpointWithMissingAuthorization() throws Exception {
        // Perform request without Authorization header
        mockMvc.perform(post("/oauth/token")
                        .param("grant_type", "client_credentials")
                        .param("scope", "read")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Missing or invalid Authorization header"));
    }
}
