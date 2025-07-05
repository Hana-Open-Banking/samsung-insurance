package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.InsuranceProduct;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.InsuranceContractRepository;
import com.insurance.samsung.backend.repository.InsuranceProductRepository;
import com.insurance.samsung.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InsuranceContractService {

    private final InsuranceContractRepository contractRepository;
    private final UserRepository userRepository;
    private final InsuranceProductRepository productRepository;

    @Autowired
    public InsuranceContractService(InsuranceContractRepository contractRepository, 
                                   UserRepository userRepository,
                                   InsuranceProductRepository productRepository) {
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<InsuranceContract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<InsuranceContract> getContractById(String insuId) {
        return contractRepository.findById(insuId);
    }

    @Transactional(readOnly = true)
    public Optional<InsuranceContract> getContractByInsuNum(String insuNum) {
        return contractRepository.findByInsuNum(insuNum);
    }

    @Transactional(readOnly = true)
    public List<InsuranceContract> getContractsByUserSeqNo(String userSeqNo) {
        return contractRepository.findByUserUserSeqNo(userSeqNo);
    }

    @Transactional(readOnly = true)
    public List<InsuranceContract> getContractsByUser(User user) {
        return contractRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<InsuranceContract> getContractsByUserSeqNoWithValidation(String userSeqNo) {
        Optional<User> userOpt = userRepository.findById(userSeqNo);
        if (userOpt.isPresent()) {
            return contractRepository.findByUser(userOpt.get());
        }
        return Collections.emptyList();
    }

    @Transactional
    public InsuranceContract createContract(InsuranceContract contract, String userSeqNo, String productId) {
        // Validate user
        User user = userRepository.findById(userSeqNo)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userSeqNo));

        // Validate product
        InsuranceProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Set relationships
        contract.setUser(user);
        contract.setProduct(product);

        // Generate masked insurance number
        if (contract.getInsuNum() != null && !contract.getInsuNum().isEmpty()) {
            contract.setInsuNumMasked(maskInsuNum(contract.getInsuNum()));
        } else {
            throw new IllegalArgumentException("Insurance number cannot be empty");
        }

        // Validate and set fixed-length fields
        validateFixedLengthFields(contract);

        // Set creation timestamp
        contract.setCreatedAt(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    @Transactional
    public InsuranceContract updateContract(String insuId, InsuranceContract updatedContract) {
        InsuranceContract existingContract = contractRepository.findById(insuId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with ID: " + insuId));

        // Update fields
        if (updatedContract.getInsuNum() != null && !updatedContract.getInsuNum().equals(existingContract.getInsuNum())) {
            existingContract.setInsuNum(updatedContract.getInsuNum());
            existingContract.setInsuNumMasked(maskInsuNum(updatedContract.getInsuNum()));
        }

        if (updatedContract.getInsuType() != null) {
            existingContract.setInsuType(updatedContract.getInsuType());
        }

        if (updatedContract.getInsuStatus() != null) {
            existingContract.setInsuStatus(updatedContract.getInsuStatus());
        }

        if (updatedContract.getIssueDate() != null) {
            existingContract.setIssueDate(updatedContract.getIssueDate());
        }

        if (updatedContract.getExpDate() != null) {
            existingContract.setExpDate(updatedContract.getExpDate());
        }

        if (updatedContract.getPremium() != null) {
            existingContract.setPremium(updatedContract.getPremium());
        }

        if (updatedContract.getPaidPremium() != null) {
            existingContract.setPaidPremium(updatedContract.getPaidPremium());
        }

        if (updatedContract.getSpecialYn() != '\0') {
            existingContract.setSpecialYn(updatedContract.getSpecialYn());
        }

        // Validate fixed-length fields
        validateFixedLengthFields(existingContract);

        return contractRepository.save(existingContract);
    }

    private String maskInsuNum(String insuNum) {
        if (insuNum == null || insuNum.length() < 8) {
            return insuNum;
        }

        // Mask the middle part of the insurance number
        // Example: "1234567890" -> "123****890"
        int visibleChars = Math.min(3, insuNum.length() / 3);
        String prefix = insuNum.substring(0, visibleChars);
        String suffix = insuNum.substring(insuNum.length() - visibleChars);
        String masked = "*".repeat(insuNum.length() - (2 * visibleChars));

        return prefix + masked + suffix;
    }

    private void validateFixedLengthFields(InsuranceContract contract) {
        // Validate insuType (2 characters)
        if (contract.getInsuType() == null || contract.getInsuType().length != 2) {
            throw new IllegalArgumentException("Insurance type must be 2 characters");
        }

        // Validate insuStatus (2 characters)
        if (contract.getInsuStatus() == null || contract.getInsuStatus().length != 2) {
            throw new IllegalArgumentException("Insurance status must be 2 characters");
        }

        // Validate specialYn (1 character: 'Y' or 'N')
        if (contract.getSpecialYn() != 'Y' && contract.getSpecialYn() != 'N') {
            throw new IllegalArgumentException("Special YN must be 'Y' or 'N'");
        }
    }
}
