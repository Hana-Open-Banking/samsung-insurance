package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_transaction_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTransactionLog {

    @Id
    @Column(name = "api_tran_id", length = 40)
    private String apiTranId;

    @Column(name = "api_tran_dtm", length = 17, nullable = false)
    private char[] apiTranDtm;

    @Column(name = "rsp_code", length = 5, nullable = false)
    private char[] rspCode;

    @Column(name = "rsp_message", length = 300, nullable = false)
    private String rspMessage;

    @Column(name = "bank_tran_id", length = 20, nullable = false)
    private String bankTranId;

    @Column(name = "bank_tran_date", length = 8, nullable = false)
    private char[] bankTranDate;

    @Column(name = "bank_code_tran", length = 3, nullable = false)
    private char[] bankCodeTran;

    @Column(name = "bank_rsp_code", length = 3, nullable = false)
    private char[] bankRspCode;

    @Column(name = "bank_rsp_message", length = 100, nullable = false)
    private String bankRspMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq_no")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insu_id")
    private InsuranceContract insuranceContract;
}