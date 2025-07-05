package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsuranceApiListResponse {
    
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
    
    @JsonProperty("insu_cnt")
    private String insuCnt;
    
    @JsonProperty("insu_list")
    private List<InsuranceInfo> insuList;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InsuranceInfo {
        
        @JsonProperty("insu_num")
        private String insuNum;
        
        @JsonProperty("prod_name")
        private String prodName;
        
        @JsonProperty("insu_type")
        private String insuType;
        
        @JsonProperty("insu_status")
        private String insuStatus;
        
        @JsonProperty("issue_date")
        private String issueDate;
        
        @JsonProperty("exp_date")
        private String expDate;
    }
} 