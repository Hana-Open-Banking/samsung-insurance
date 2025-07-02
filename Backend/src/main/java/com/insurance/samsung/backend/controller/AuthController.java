package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.LoginRequestDto;
import com.insurance.samsung.backend.dto.LoginResponseDto;
import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.service.OAuthService;
import com.insurance.samsung.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OAuthService oauthService;

    @Autowired
    public AuthController(UserService userService, OAuthService oauthService) {
        this.userService = userService;
        this.oauthService = oauthService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        // Authenticate user
        System.out.println("login request email: " + loginRequest.getUserEmail());
        System.out.println("login request email: " + loginRequest.getPassword());
        Optional<User> userOpt = userService.authenticateUser(loginRequest.getUserEmail(), loginRequest.getPassword());

        if (userOpt.isEmpty()) {
            return createErrorResponse("INVALID_CREDENTIALS", "Invalid user ID or password", HttpStatus.UNAUTHORIZED);
        }

        User user = userOpt.get();

        // Issue token for user
        Optional<OAuthToken> tokenOpt = oauthService.issueTokenForUser("CLIENT001", "secret123", "read", user);

        if (tokenOpt.isEmpty()) {
            return createErrorResponse("TOKEN_ISSUE_FAILED", "Failed to issue access token", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        OAuthToken token = tokenOpt.get();

        // Create response
        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken(token.getAccessTokenId())
                .userSeqNo(user.getUserSeqNo())
                .build();

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, String description, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("error_description", description);
        return ResponseEntity.status(status).body(response);
    }
}
