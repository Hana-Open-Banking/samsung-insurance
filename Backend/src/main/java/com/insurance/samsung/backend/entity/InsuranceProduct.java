package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "insurance_product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceProduct {

    @Id
    @Column(name = "product_id", length = 10)
    private String productId;

    @Column(name = "product_type", length = 2, nullable = false)
    private String productType;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "total_premium", nullable = false)
    private Long totalPremium;
}