package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsuranceContractDto {
    private String insuId;
    private String insuNum;
    private String userSeqNo;
    private String productId;
    private String productName;
    private String insuType;
    private String insuStatus;
    private String issueDate;
    private String expDate;
    private Long premium;
    private Long paidPremium;
    private String specialYn;
    private LocalDateTime createdAt;
}