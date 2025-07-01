package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.InsuranceProductDto;
import com.insurance.samsung.backend.entity.InsuranceProduct;
import com.insurance.samsung.backend.service.InsuranceProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class InsuranceProductController {

    private final InsuranceProductService productService;

    @Autowired
    public InsuranceProductController(InsuranceProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<InsuranceProductDto>> getAllProducts() {
        List<InsuranceProduct> products = productService.getAllProducts();
        List<InsuranceProductDto> productDtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InsuranceProductDto> getProductById(@PathVariable String productId) {
        Optional<InsuranceProduct> productOpt = productService.getProductById(productId);
        return productOpt.map(product -> ResponseEntity.ok(convertToDto(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{productType}")
    public ResponseEntity<List<InsuranceProductDto>> getProductsByType(@PathVariable String productType) {
        List<InsuranceProduct> products = productService.getProductsByType(productType);
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<InsuranceProductDto> productDtos = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    private InsuranceProductDto convertToDto(InsuranceProduct product) {
        return InsuranceProductDto.builder()
                .productId(product.getProductId())
                .productType(product.getProductType())
                .productName(product.getProductName())
                .totalPremium(product.getTotalPremium())
                .build();
    }
}