package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceContractRepository extends JpaRepository<InsuranceContract, String> {
    
    List<InsuranceContract> findByUser(User user);
    
    List<InsuranceContract> findByUserUserSeqNo(String userSeqNo);
    
    Optional<InsuranceContract> findByInsuNum(String insuNum);
}