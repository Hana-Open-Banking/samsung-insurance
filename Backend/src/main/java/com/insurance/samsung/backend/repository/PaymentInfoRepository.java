package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, String> {
    Optional<PaymentInfo> findByInsuranceContract(InsuranceContract insuranceContract);
    Optional<PaymentInfo> findByInsuranceContract_InsuId(String insuId);
}