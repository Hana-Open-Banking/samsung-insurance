package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInfoDto {
    private String paymentId;
    private String insuId;
    private String insuNum;
    private String insuNumMasked;
    private String payDue;
    private String payCycle;
    private String payDate;
    private String payEndDate;
    private BigDecimal payAmt;
    private String payOrgCode;
    private String payAccountNum;
    private String payAccountNumMasked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}