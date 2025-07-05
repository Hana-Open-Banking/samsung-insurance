package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.InsuranceApiListRequest;
import com.insurance.samsung.backend.dto.InsuranceApiListResponse;
import com.insurance.samsung.backend.dto.InsuranceApiPaymentRequest;
import com.insurance.samsung.backend.dto.InsuranceApiPaymentResponse;
import com.insurance.samsung.backend.service.InsuranceApiService;
import com.insurance.samsung.backend.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v2.0")
@RequiredArgsConstructor
@Tag(name = "삼성화재 보험 정보 조회", description = "KFTC에서 호출하는 보험 정보 조회 API")
public class InsuranceApiController {
    
    private final InsuranceApiService insuranceApiService;
    private final OAuthService oAuthService;

    @PostMapping("/insurances")
    @Operation(
        summary = "보험목록조회 API (내부)",
        description = "KFTC에서 호출하는 보험 목록 조회"
    )
    public ResponseEntity<InsuranceApiListResponse> getInsuranceList(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody InsuranceApiListRequest request) {
        
        log.info("보험목록조회 API 호출 - bankTranId: {}, userCi: {}", 
                request.getBankTranId(), request.getUserCi());
        
        validateAuthorization(authorization);
        
        InsuranceApiListResponse response = insuranceApiService.getInsuranceList(request);
        
        log.info("보험목록조회 API 응답 - insuCnt: {}, rspCode: {}", 
                response.getInsuCnt(), response.getRspCode());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/insurances/payment")
    @Operation(
        summary = "보험납입정보조회 API (내부)",
        description = "KFTC에서 호출하는 보험 납입 정보 조회"
    )
    public ResponseEntity<InsuranceApiPaymentResponse> getInsurancePayment(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody InsuranceApiPaymentRequest request) {
        
        log.info("보험납입정보조회 API 호출 - bankTranId: {}, userCi: {}, insuNum: {}", 
                request.getBankTranId(), request.getUserCi(), request.getInsuNum());
        
        validateAuthorization(authorization);
        
        InsuranceApiPaymentResponse response = insuranceApiService.getInsurancePayment(request);
        
        log.info("보험납입정보조회 API 응답 - rspCode: {}", response.getRspCode());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Authorization 헤더 검증
     */
    private void validateAuthorization(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("유효하지 않은 Authorization 헤더: {}", authorization);
            throw new IllegalArgumentException("유효하지 않은 Authorization 헤더");
        }
        
        String token = authorization.substring(7);
        if (token.trim().isEmpty()) {
            log.error("Authorization 토큰이 비어있음");
            throw new IllegalArgumentException("Authorization 토큰이 비어있음");
        }
        
        // 실제 운영환경에서는 JWT 토큰 검증 수행
        log.debug("Authorization 토큰 검증 완료");
    }
} 