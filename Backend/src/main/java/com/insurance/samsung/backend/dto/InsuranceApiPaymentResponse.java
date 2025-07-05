package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsuranceApiPaymentResponse {
    
    @JsonProperty("api_tran_id")
    private String apiTranId;
    
    @JsonProperty("api_tran_dtm")
    private String apiTranDtm;
    
    @JsonProperty("rsp_code")
    private String rspCode;
    
    @JsonProperty("rsp_message")
    private String rspMessage;
    
    @JsonProperty("bank_tran_id")
    private String bankTranId;
    
    @JsonProperty("bank_code_std")
    private String bankCodeStd;
    
    @JsonProperty("member_bank_code")
    private String memberBankCode;
    
    @JsonProperty("user_ci")
    private String userCi;
    
    @JsonProperty("insu_num")
    private String insuNum;
    
    @JsonProperty("pay_due")
    private String payDue;
    
    @JsonProperty("pay_cycle")
    private String payCycle;
    
    @JsonProperty("pay_date")
    private String payDate;
    
    @JsonProperty("pay_end_date")
    private String payEndDate;
    
    @JsonProperty("pay_amt")
    private String payAmt;
    
    @JsonProperty("pay_org_code")
    private String payOrgCode;
    
    @JsonProperty("pay_account_num_masked")
    private String payAccountNumMasked;
} 