package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.InsuranceContractRepository;
import com.insurance.samsung.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InsuranceContractService {

    private final InsuranceContractRepository contractRepository;
    private final UserRepository userRepository;

    @Autowired
    public InsuranceContractService(InsuranceContractRepository contractRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
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
}