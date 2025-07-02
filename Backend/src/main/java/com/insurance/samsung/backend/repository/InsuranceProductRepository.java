package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.InsuranceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceProductRepository extends JpaRepository<InsuranceProduct, String> {
    
    List<InsuranceProduct> findByProductType(String productType);
}