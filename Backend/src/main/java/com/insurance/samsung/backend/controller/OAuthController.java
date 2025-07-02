package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.OAuthTokenDto;
import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oauthService;

    @Autowired
    public OAuthController(OAuthService oauthService) {
        this.oauthService = oauthService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(
            @RequestParam("grant_type") String grantType,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "refresh_token", required = false) String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // Handle client credentials grant type
        if ("client_credentials".equals(grantType)) {
            if (authorization == null || !authorization.startsWith("Basic ")) {
                return createErrorResponse("invalid_request", "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Extract client credentials from Authorization header
            String base64Credentials = authorization.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);

            if (parts.length != 2) {
                return createErrorResponse("invalid_client", "Invalid client credentials format", HttpStatus.UNAUTHORIZED);
            }

            String clientId = parts[0];
            String clientSecret = parts[1];

            // Issue token
            Optional<OAuthToken> tokenOpt = oauthService.issueToken(clientId, clientSecret, scope);

            if (tokenOpt.isPresent()) {
                return ResponseEntity.ok(convertToDto(tokenOpt.get()));
            } else {
                return createErrorResponse("invalid_client", "Invalid client credentials", HttpStatus.UNAUTHORIZED);
            }
        }
        // Handle refresh token grant type
        else if ("refresh_token".equals(grantType)) {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return createErrorResponse("invalid_request", "Missing refresh token", HttpStatus.BAD_REQUEST);
            }

            Optional<OAuthToken> tokenOpt = oauthService.refreshToken(refreshToken);

            if (tokenOpt.isPresent()) {
                return ResponseEntity.ok(convertToDto(tokenOpt.get()));
            } else {
                return createErrorResponse("invalid_grant", "Invalid refresh token", HttpStatus.BAD_REQUEST);
            }
        }
        // Unsupported grant type
        else {
            return createErrorResponse("unsupported_grant_type", "Unsupported grant type: " + grantType, HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, String description, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("error_description", description);
        return ResponseEntity.status(status).body(response);
    }

    private OAuthTokenDto convertToDto(OAuthToken token) {
        return OAuthTokenDto.builder()
                .accessToken(token.getAccessTokenId())
                .tokenType("Bearer")
                .expiresIn(token.getExpiresIn())
                .refreshToken(token.getRefreshToken())
                .scope(token.getScope())
                .build();
    }
}
