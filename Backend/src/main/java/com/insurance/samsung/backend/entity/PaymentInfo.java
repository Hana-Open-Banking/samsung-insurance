package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfo {

    @Id
    @Column(name = "payment_id", length = 20)
    private String paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insu_id", nullable = false)
    private InsuranceContract insuranceContract;

    @Column(name = "pay_due", length = 2, nullable = false)
    private char[] payDue;

    @Column(name = "pay_cycle", length = 2, nullable = false)
    private char[] payCycle;

    @Column(name = "pay_date", length = 2, nullable = false)
    private char[] payDate;

    @Column(name = "pay_end_date", length = 8, nullable = false)
    private char[] payEndDate;

    @Column(name = "pay_amt", precision = 12, nullable = false)
    private BigDecimal payAmt;

    @Column(name = "pay_org_code", length = 3, nullable = false)
    private char[] payOrgCode;

    @Column(name = "pay_account_num", length = 16, nullable = false)
    private String payAccountNum;

    @Column(name = "pay_account_num_masked", length = 20, nullable = false)
    private String payAccountNumMasked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}