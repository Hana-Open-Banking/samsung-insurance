package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.OAuthClient;
import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.OAuthClientRepository;
import com.insurance.samsung.backend.repository.OAuthTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class OAuthService {

    private final OAuthClientRepository clientRepository;
    private final OAuthTokenRepository tokenRepository;

    @Autowired
    public OAuthService(OAuthClientRepository clientRepository, OAuthTokenRepository tokenRepository) {
        this.clientRepository = clientRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public Optional<OAuthToken> issueToken(String clientId, String clientSecret, String scope) {
        Optional<OAuthClient> clientOpt = clientRepository.findByClientIdAndClientSecret(clientId, clientSecret);

        if (clientOpt.isPresent()) {
            OAuthClient client = clientOpt.get();

            // Generate tokens
            String accessTokenId = UUID.randomUUID().toString();
            String refreshToken = UUID.randomUUID().toString();

            // Create token entity
            OAuthToken token = OAuthToken.builder()
                    .accessTokenId(accessTokenId)
                    .client(client)
                    .expiresIn(3600) // 1 hour
                    .refreshToken(refreshToken)
                    .scope(scope)
                    .issuedAt(LocalDateTime.now())
                    .build();

            return Optional.of(tokenRepository.save(token));
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<OAuthToken> issueTokenForUser(String clientId, String clientSecret, String scope, User user) {
        Optional<OAuthClient> clientOpt = clientRepository.findByClientIdAndClientSecret(clientId, clientSecret);

        if (clientOpt.isPresent()) {
            OAuthClient client = clientOpt.get();

            // Generate tokens
            String accessTokenId = UUID.randomUUID().toString();
            String refreshToken = UUID.randomUUID().toString();

            // Create token entity with user
            OAuthToken token = OAuthToken.builder()
                    .accessTokenId(accessTokenId)
                    .client(client)
                    .user(user)
                    .expiresIn(3600) // 1 hour
                    .refreshToken(refreshToken)
                    .scope(scope)
                    .issuedAt(LocalDateTime.now())
                    .build();

            return Optional.of(tokenRepository.save(token));
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<OAuthToken> refreshToken(String refreshToken) {
        Optional<OAuthToken> tokenOpt = tokenRepository.findByRefreshToken(refreshToken);

        if (tokenOpt.isPresent()) {
            OAuthToken oldToken = tokenOpt.get();

            // Check if the token is still valid
            if (oldToken.getIssuedAt().plusSeconds(oldToken.getExpiresIn() * 2).isAfter(LocalDateTime.now())) {
                // Generate new access token
                String newAccessTokenId = UUID.randomUUID().toString();

                // Update token
                oldToken.setAccessTokenId(newAccessTokenId);
                oldToken.setRefreshedAt(LocalDateTime.now());

                return Optional.of(tokenRepository.save(oldToken));
            }
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<OAuthToken> validateToken(String accessTokenId) {
        Optional<OAuthToken> tokenOpt = tokenRepository.findByAccessTokenId(accessTokenId);

        if (tokenOpt.isPresent()) {
            OAuthToken token = tokenOpt.get();

            // Check if the token is still valid
            if (token.getIssuedAt().plusSeconds(token.getExpiresIn()).isAfter(LocalDateTime.now())) {
                return Optional.of(token);
            }
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByToken(String accessTokenId) {
        Optional<OAuthToken> tokenOpt = validateToken(accessTokenId);

        if (tokenOpt.isPresent() && tokenOpt.get().getUser() != null) {
            return Optional.of(tokenOpt.get().getUser());
        }

        return Optional.empty();
    }
}
