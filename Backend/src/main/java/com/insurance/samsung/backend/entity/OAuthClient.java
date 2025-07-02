package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_client")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClient {

    @Id
    @Column(name = "client_id", length = 10)
    private String clientId;

    @Column(name = "client_secret", length = 100, nullable = false)
    private String clientSecret;

    @Column(name = "client_name", length = 100, nullable = false)
    private String clientName;
}