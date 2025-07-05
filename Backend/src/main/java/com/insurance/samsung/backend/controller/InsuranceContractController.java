package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.InsuranceContractDto;
import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.OAuthToken;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.service.InsuranceContractService;
import com.insurance.samsung.backend.service.OAuthService;
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
public class InsuranceContractController {

    private final InsuranceContractService contractService;
    private final OAuthService oauthService;

    @Autowired
    public InsuranceContractController(InsuranceContractService contractService, OAuthService oauthService) {
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

    @GetMapping("/contracts/{insuId}")
    public ResponseEntity<?> getContractById(
            @PathVariable String insuId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // 토큰 유효성 검사
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        Optional<OAuthToken> tokenOpt = oauthService.validateToken(token);
        if (tokenOpt.isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        // 토큰에 연결된 사용자 확인
        Optional<User> userOpt = oauthService.getUserByToken(token);
        if (userOpt.isEmpty()) {
            return createErrorResponse("USER_NOT_FOUND", "토큰에 연결된 사용자가 없습니다", HttpStatus.UNAUTHORIZED);
        }

        // 계약 조회
        Optional<InsuranceContract> contractOpt = contractService.getContractById(insuId);
        if (contractOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 사용자가 계약 소유자인지 확인
        InsuranceContract contract = contractOpt.get();
        if (!contract.getUser().getUserSeqNo().equals(userOpt.get().getUserSeqNo())) {
            return createErrorResponse("FORBIDDEN", "해당 보험 계약에 접근 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(convertToDto(contract));
    }

    @GetMapping("/user/{userSeqNo}/contracts")
    public ResponseEntity<?> getContractsByUserSeqNo(
            @PathVariable String userSeqNo,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        List<InsuranceContract> contracts = contractService.getContractsByUserSeqNo(userSeqNo);
        if (contracts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<InsuranceContractDto> contractDtos = contracts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contractDtos);
    }

    private InsuranceContractDto convertToDto(InsuranceContract contract) {
        return InsuranceContractDto.builder()
                .insuId(contract.getInsuId())
                .insuNum(contract.getInsuNum())
                .userSeqNo(contract.getUser().getUserSeqNo())
                .productId(contract.getProduct().getProductId())
                .productName(contract.getProduct().getProdName())
                .insuType(new String(contract.getInsuType()))
                .insuStatus(new String(contract.getInsuStatus()))
                .issueDate(contract.getIssueDate())
                .expDate(contract.getExpDate())
                .premium(contract.getPremium())
                .paidPremium(contract.getPaidPremium())
                .specialYn(String.valueOf(contract.getSpecialYn()))
                .createdAt(contract.getCreatedAt())
                .build();
    }
}
