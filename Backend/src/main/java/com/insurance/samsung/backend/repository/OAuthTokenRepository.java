package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.OAuthClient;
import com.insurance.samsung.backend.entity.OAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthTokenRepository extends JpaRepository<OAuthToken, String> {
    
    Optional<OAuthToken> findByAccessTokenId(String accessTokenId);
    
    Optional<OAuthToken> findByRefreshToken(String refreshToken);
    
    void deleteByClient(OAuthClient client);
}