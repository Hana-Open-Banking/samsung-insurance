package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.PaymentInfo;
import com.insurance.samsung.backend.repository.InsuranceContractRepository;
import com.insurance.samsung.backend.repository.PaymentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;
    private final InsuranceContractRepository contractRepository;

    @Autowired
    public PaymentInfoService(PaymentInfoRepository paymentInfoRepository, 
                             InsuranceContractRepository contractRepository) {
        this.paymentInfoRepository = paymentInfoRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional(readOnly = true)
    public List<PaymentInfo> getAllPaymentInfos() {
        return paymentInfoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PaymentInfo> getPaymentInfoById(String paymentId) {
        return paymentInfoRepository.findById(paymentId);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentInfo> getPaymentInfoByContractId(String insuId) {
        return paymentInfoRepository.findByInsuranceContract_InsuId(insuId);
    }

    @Transactional
    public PaymentInfo createPaymentInfo(PaymentInfo paymentInfo, String insuId) {
        // Validate insurance contract
        InsuranceContract contract = contractRepository.findById(insuId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance contract not found with ID: " + insuId));
        
        // Set relationship
        paymentInfo.setInsuranceContract(contract);
        
        // Validate fixed-length fields
        validateFixedLengthFields(paymentInfo);
        
        // Generate masked account number
        if (paymentInfo.getPayAccountNum() != null && !paymentInfo.getPayAccountNum().isEmpty()) {
            paymentInfo.setPayAccountNumMasked(maskAccountNumber(paymentInfo.getPayAccountNum()));
        } else {
            throw new IllegalArgumentException("Payment account number cannot be empty");
        }
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        paymentInfo.setCreatedAt(now);
        paymentInfo.setUpdatedAt(now);
        
        return paymentInfoRepository.save(paymentInfo);
    }

    @Transactional
    public PaymentInfo updatePaymentInfo(String paymentId, PaymentInfo updatedPaymentInfo) {
        PaymentInfo existingPaymentInfo = paymentInfoRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment info not found with ID: " + paymentId));
        
        // Update fields
        if (updatedPaymentInfo.getPayDue() != null) {
            existingPaymentInfo.setPayDue(updatedPaymentInfo.getPayDue());
        }
        
        if (updatedPaymentInfo.getPayCycle() != null) {
            existingPaymentInfo.setPayCycle(updatedPaymentInfo.getPayCycle());
        }
        
        if (updatedPaymentInfo.getPayDate() != null) {
            existingPaymentInfo.setPayDate(updatedPaymentInfo.getPayDate());
        }
        
        if (updatedPaymentInfo.getPayEndDate() != null) {
            existingPaymentInfo.setPayEndDate(updatedPaymentInfo.getPayEndDate());
        }
        
        if (updatedPaymentInfo.getPayAmt() != null) {
            existingPaymentInfo.setPayAmt(updatedPaymentInfo.getPayAmt());
        }
        
        if (updatedPaymentInfo.getPayOrgCode() != null) {
            existingPaymentInfo.setPayOrgCode(updatedPaymentInfo.getPayOrgCode());
        }
        
        if (updatedPaymentInfo.getPayAccountNum() != null && !updatedPaymentInfo.getPayAccountNum().isEmpty()) {
            existingPaymentInfo.setPayAccountNum(updatedPaymentInfo.getPayAccountNum());
            existingPaymentInfo.setPayAccountNumMasked(maskAccountNumber(updatedPaymentInfo.getPayAccountNum()));
        }
        
        // Validate fixed-length fields
        validateFixedLengthFields(existingPaymentInfo);
        
        // Update timestamp
        existingPaymentInfo.setUpdatedAt(LocalDateTime.now());
        
        return paymentInfoRepository.save(existingPaymentInfo);
    }

    private String maskAccountNumber(String accountNum) {
        if (accountNum == null || accountNum.length() < 8) {
            return accountNum;
        }
        
        // Mask the middle part of the account number
        // Example: "1234567890" -> "123****890"
        int visibleChars = Math.min(3, accountNum.length() / 3);
        String prefix = accountNum.substring(0, visibleChars);
        String suffix = accountNum.substring(accountNum.length() - visibleChars);
        String masked = "*".repeat(accountNum.length() - (2 * visibleChars));
        
        return prefix + masked + suffix;
    }

    private void validateFixedLengthFields(PaymentInfo paymentInfo) {
        // Validate payDue (2 characters)
        if (paymentInfo.getPayDue() == null || paymentInfo.getPayDue().length != 2) {
            throw new IllegalArgumentException("Pay due must be 2 characters");
        }
        
        // Validate payCycle (2 characters)
        if (paymentInfo.getPayCycle() == null || paymentInfo.getPayCycle().length != 2) {
            throw new IllegalArgumentException("Pay cycle must be 2 characters");
        }
        
        // Validate payDate (2 characters)
        if (paymentInfo.getPayDate() == null || paymentInfo.getPayDate().length != 2) {
            throw new IllegalArgumentException("Pay date must be 2 characters");
        }
        
        // Validate payEndDate (8 characters)
        if (paymentInfo.getPayEndDate() == null || paymentInfo.getPayEndDate().length != 8) {
            throw new IllegalArgumentException("Pay end date must be 8 characters in YYYYMMDD format");
        }
        
        // Validate payOrgCode (3 characters)
        if (paymentInfo.getPayOrgCode() == null || paymentInfo.getPayOrgCode().length != 3) {
            throw new IllegalArgumentException("Pay organization code must be 3 characters");
        }
    }
}