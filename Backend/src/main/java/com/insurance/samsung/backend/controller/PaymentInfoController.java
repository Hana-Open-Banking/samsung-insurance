package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.PaymentInfoDto;
import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.PaymentInfo;
import com.insurance.samsung.backend.service.InsuranceContractService;
import com.insurance.samsung.backend.service.OAuthService;
import com.insurance.samsung.backend.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;
    private final InsuranceContractService contractService;
    private final OAuthService oauthService;

    @Autowired
    public PaymentInfoController(PaymentInfoService paymentInfoService, 
                                InsuranceContractService contractService,
                                OAuthService oauthService) {
        this.paymentInfoService = paymentInfoService;
        this.contractService = contractService;
        this.oauthService = oauthService;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, String description, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("error_description", description);
        return ResponseEntity.status(status).body(response);
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring("Bearer ".length());
        }
        return null;
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<?> getPaymentInfoById(
            @PathVariable String paymentId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        Optional<PaymentInfo> paymentInfoOpt = paymentInfoService.getPaymentInfoById(paymentId);
        return paymentInfoOpt.map(paymentInfo -> ResponseEntity.ok(convertToDto(paymentInfo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contracts/{insuId}/payment")
    public ResponseEntity<?> getPaymentInfoByContractId(
            @PathVariable String insuId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        Optional<PaymentInfo> paymentInfoOpt = paymentInfoService.getPaymentInfoByContractId(insuId);
        return paymentInfoOpt.map(paymentInfo -> ResponseEntity.ok(convertToDto(paymentInfo)))
                .orElse(ResponseEntity.notFound().build());
    }

    private PaymentInfoDto convertToDto(PaymentInfo paymentInfo) {
        InsuranceContract contract = paymentInfo.getInsuranceContract();
        return PaymentInfoDto.builder()
                .paymentId(paymentInfo.getPaymentId())
                .insuId(contract.getInsuId())
                .insuNum(contract.getInsuNum())
                .insuNumMasked(contract.getInsuNumMasked())
                .payDue(new String(paymentInfo.getPayDue()))
                .payCycle(new String(paymentInfo.getPayCycle()))
                .payDate(new String(paymentInfo.getPayDate()))
                .payEndDate(new String(paymentInfo.getPayEndDate()))
                .payAmt(paymentInfo.getPayAmt())
                .payOrgCode(new String(paymentInfo.getPayOrgCode()))
                .payAccountNum(paymentInfo.getPayAccountNum())
                .payAccountNumMasked(paymentInfo.getPayAccountNumMasked())
                .createdAt(paymentInfo.getCreatedAt())
                .updatedAt(paymentInfo.getUpdatedAt())
                .build();
    }
}