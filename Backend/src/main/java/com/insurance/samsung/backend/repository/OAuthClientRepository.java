package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {
    
    Optional<OAuthClient> findByClientIdAndClientSecret(String clientId, String clientSecret);
}