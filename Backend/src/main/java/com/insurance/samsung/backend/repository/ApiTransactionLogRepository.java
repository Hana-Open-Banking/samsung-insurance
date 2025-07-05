package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.ApiTransactionLog;
import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiTransactionLogRepository extends JpaRepository<ApiTransactionLog, String> {
    List<ApiTransactionLog> findByUser(User user);
    List<ApiTransactionLog> findByUser_UserSeqNo(String userSeqNo);
    List<ApiTransactionLog> findByInsuranceContract(InsuranceContract insuranceContract);
    List<ApiTransactionLog> findByInsuranceContract_InsuId(String insuId);
}