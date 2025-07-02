package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthToken {

    @Id
    @Column(name = "access_token_id", length = 100)
    private String accessTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private OAuthClient client;

    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    @Column(name = "refresh_token", length = 100, nullable = false)
    private String refreshToken;

    @Column(name = "scope", length = 50)
    private String scope;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "refreshed_at")
    private LocalDateTime refreshedAt;
}