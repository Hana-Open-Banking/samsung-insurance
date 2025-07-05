package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.ApiTransactionLog;
import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.ApiTransactionLogRepository;
import com.insurance.samsung.backend.repository.InsuranceContractRepository;
import com.insurance.samsung.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ApiTransactionLogService {

    private final ApiTransactionLogRepository transactionLogRepository;
    private final UserRepository userRepository;
    private final InsuranceContractRepository contractRepository;

    @Autowired
    public ApiTransactionLogService(ApiTransactionLogRepository transactionLogRepository,
                                   UserRepository userRepository,
                                   InsuranceContractRepository contractRepository) {
        this.transactionLogRepository = transactionLogRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }

    @Transactional(readOnly = true)
    public List<ApiTransactionLog> getAllTransactionLogs() {
        return transactionLogRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ApiTransactionLog> getTransactionLogById(String apiTranId) {
        return transactionLogRepository.findById(apiTranId);
    }

    @Transactional(readOnly = true)
    public List<ApiTransactionLog> getTransactionLogsByUser(String userSeqNo) {
        return transactionLogRepository.findByUser_UserSeqNo(userSeqNo);
    }

    @Transactional(readOnly = true)
    public List<ApiTransactionLog> getTransactionLogsByContract(String insuId) {
        return transactionLogRepository.findByInsuranceContract_InsuId(insuId);
    }

    @Transactional
    public ApiTransactionLog createTransactionLog(ApiTransactionLog transactionLog, 
                                                 String userSeqNo, 
                                                 String insuId) {
        // Set user if provided
        if (userSeqNo != null && !userSeqNo.isEmpty()) {
            User user = userRepository.findById(userSeqNo)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userSeqNo));
            transactionLog.setUser(user);
        }
        
        // Set insurance contract if provided
        if (insuId != null && !insuId.isEmpty()) {
            InsuranceContract contract = contractRepository.findById(insuId)
                    .orElseThrow(() -> new IllegalArgumentException("Insurance contract not found with ID: " + insuId));
            transactionLog.setInsuranceContract(contract);
        }
        
        // Validate fixed-length fields
        validateFixedLengthFields(transactionLog);
        
        // Generate API transaction date time if not provided
        if (transactionLog.getApiTranDtm() == null || transactionLog.getApiTranDtm().length == 0) {
            transactionLog.setApiTranDtm(generateCurrentDateTime());
        }
        
        return transactionLogRepository.save(transactionLog);
    }

    @Transactional
    public ApiTransactionLog updateTransactionLog(String apiTranId, ApiTransactionLog updatedLog) {
        ApiTransactionLog existingLog = transactionLogRepository.findById(apiTranId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction log not found with ID: " + apiTranId));
        
        // Update fields
        if (updatedLog.getRspCode() != null) {
            existingLog.setRspCode(updatedLog.getRspCode());
        }
        
        if (updatedLog.getRspMessage() != null) {
            existingLog.setRspMessage(updatedLog.getRspMessage());
        }
        
        if (updatedLog.getBankRspCode() != null) {
            existingLog.setBankRspCode(updatedLog.getBankRspCode());
        }
        
        if (updatedLog.getBankRspMessage() != null) {
            existingLog.setBankRspMessage(updatedLog.getBankRspMessage());
        }
        
        // Validate fixed-length fields
        validateFixedLengthFields(existingLog);
        
        return transactionLogRepository.save(existingLog);
    }

    private char[] generateCurrentDateTime() {
        // Format: YYYYMMDDHHmmssSSS (17 characters)
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String dateTimeStr = now.format(formatter);
        return dateTimeStr.toCharArray();
    }

    private void validateFixedLengthFields(ApiTransactionLog transactionLog) {
        // Validate apiTranDtm (17 characters)
        if (transactionLog.getApiTranDtm() == null || transactionLog.getApiTranDtm().length != 17) {
            throw new IllegalArgumentException("API transaction date time must be 17 characters");
        }
        
        // Validate rspCode (5 characters)
        if (transactionLog.getRspCode() == null || transactionLog.getRspCode().length != 5) {
            throw new IllegalArgumentException("Response code must be 5 characters");
        }
        
        // Validate bankTranDate (8 characters)
        if (transactionLog.getBankTranDate() == null || transactionLog.getBankTranDate().length != 8) {
            throw new IllegalArgumentException("Bank transaction date must be 8 characters in YYYYMMDD format");
        }
        
        // Validate bankCodeTran (3 characters)
        if (transactionLog.getBankCodeTran() == null || transactionLog.getBankCodeTran().length != 3) {
            throw new IllegalArgumentException("Bank code must be 3 characters");
        }
        
        // Validate bankRspCode (3 characters)
        if (transactionLog.getBankRspCode() == null || transactionLog.getBankRspCode().length != 3) {
            throw new IllegalArgumentException("Bank response code must be 3 characters");
        }
    }
}