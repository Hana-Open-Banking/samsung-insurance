package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_contract", indexes = {
    @Index(name = "idx_insu_num", columnList = "insu_num")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceContract {

    @Id
    @Column(name = "insu_id", length = 20)
    private String insuId;

    @Column(name = "insu_num", length = 20, nullable = false)
    private String insuNum;

    @Column(name = "insu_num_masked", length = 20, nullable = false)
    private String insuNumMasked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct product;

    @Column(name = "insu_type", length = 2, nullable = false)
    private char[] insuType;

    @Column(name = "insu_status", length = 2, nullable = false)
    private char[] insuStatus;

    @Column(name = "issue_date", length = 8, nullable = false)
    private String issueDate;

    @Column(name = "exp_date", length = 8, nullable = false)
    private String expDate;

    @Column(name = "premium", nullable = false)
    private Long premium;

    @Column(name = "paid_premium", nullable = false)
    private Long paidPremium;

    @Column(name = "special_yn", length = 1, nullable = false)
    private char specialYn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
