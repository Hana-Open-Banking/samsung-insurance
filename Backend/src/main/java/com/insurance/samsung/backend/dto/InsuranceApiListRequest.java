package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceApiListRequest {
    
    @NotBlank(message = "은행거래고유번호는 필수입니다")
    @JsonProperty("bank_tran_id")
    private String bankTranId;
    
    @NotBlank(message = "사용자CI는 필수입니다")
    @JsonProperty("user_ci")
    private String userCi;
    
    @NotBlank(message = "표준은행코드는 필수입니다")
    @JsonProperty("bank_code_std")
    private String bankCodeStd;
    
    @NotBlank(message = "회원은행코드는 필수입니다")
    @JsonProperty("member_bank_code")
    private String memberBankCode;
    
    @NotNull(message = "조회구분코드는 필수입니다")
    @JsonProperty("search_timestamp")
    private String searchTimestamp;
} 