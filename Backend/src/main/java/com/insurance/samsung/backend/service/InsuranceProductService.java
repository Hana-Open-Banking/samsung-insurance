package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.InsuranceProduct;
import com.insurance.samsung.backend.repository.InsuranceProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceProductService {

    private final InsuranceProductRepository productRepository;

    @Autowired
    public InsuranceProductService(InsuranceProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<InsuranceProduct> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<InsuranceProduct> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    @Transactional(readOnly = true)
    public List<InsuranceProduct> getProductsByType(String productType) {
        return productRepository.findByProductType(productType);
    }
}