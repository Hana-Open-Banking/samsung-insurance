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
        if (productType == null || productType.length() != 2) {
            throw new IllegalArgumentException("Product type must be 2 characters");
        }
        // Convert String to char[] for query
        char[] typeChars = productType.toCharArray();
        return productRepository.findByProductType(typeChars);
    }

    @Transactional
    public InsuranceProduct createProduct(InsuranceProduct product) {
        // Validate product type (2 characters)
        if (product.getProductType() == null || product.getProductType().length != 2) {
            throw new IllegalArgumentException("Product type must be 2 characters");
        }

        // Validate product name
        if (product.getProdName() == null || product.getProdName().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        return productRepository.save(product);
    }

    @Transactional
    public InsuranceProduct updateProduct(String productId, InsuranceProduct updatedProduct) {
        InsuranceProduct existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Update fields
        if (updatedProduct.getProductType() != null) {
            if (updatedProduct.getProductType().length != 2) {
                throw new IllegalArgumentException("Product type must be 2 characters");
            }
            existingProduct.setProductType(updatedProduct.getProductType());
        }

        if (updatedProduct.getProdName() != null && !updatedProduct.getProdName().isEmpty()) {
            existingProduct.setProdName(updatedProduct.getProdName());
        }

        if (updatedProduct.getTotalPremium() != null) {
            existingProduct.setTotalPremium(updatedProduct.getTotalPremium());
        }

        return productRepository.save(existingProduct);
    }
}
