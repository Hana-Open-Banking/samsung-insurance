package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.ApiTransactionLogDto;
import com.insurance.samsung.backend.entity.ApiTransactionLog;
import com.insurance.samsung.backend.service.ApiTransactionLogService;
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
public class ApiTransactionLogController {

    private final ApiTransactionLogService transactionLogService;
    private final OAuthService oauthService;

    @Autowired
    public ApiTransactionLogController(ApiTransactionLogService transactionLogService, OAuthService oauthService) {
        this.transactionLogService = transactionLogService;
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

    @GetMapping("/transactions/{apiTranId}")
    public ResponseEntity<?> getTransactionLogById(
            @PathVariable String apiTranId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        Optional<ApiTransactionLog> transactionLogOpt = transactionLogService.getTransactionLogById(apiTranId);
        return transactionLogOpt.map(transactionLog -> ResponseEntity.ok(convertToDto(transactionLog)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userSeqNo}/transactions")
    public ResponseEntity<?> getTransactionLogsByUser(
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

        List<ApiTransactionLog> transactionLogs = transactionLogService.getTransactionLogsByUser(userSeqNo);
        if (transactionLogs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ApiTransactionLogDto> transactionLogDtos = transactionLogs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionLogDtos);
    }

    @GetMapping("/contracts/{insuId}/transactions")
    public ResponseEntity<?> getTransactionLogsByContract(
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

        List<ApiTransactionLog> transactionLogs = transactionLogService.getTransactionLogsByContract(insuId);
        if (transactionLogs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ApiTransactionLogDto> transactionLogDtos = transactionLogs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionLogDtos);
    }

    private ApiTransactionLogDto convertToDto(ApiTransactionLog transactionLog) {
        ApiTransactionLogDto dto = ApiTransactionLogDto.builder()
                .apiTranId(transactionLog.getApiTranId())
                .apiTranDtm(new String(transactionLog.getApiTranDtm()))
                .rspCode(new String(transactionLog.getRspCode()))
                .rspMessage(transactionLog.getRspMessage())
                .bankTranId(transactionLog.getBankTranId())
                .bankTranDate(new String(transactionLog.getBankTranDate()))
                .bankCodeTran(new String(transactionLog.getBankCodeTran()))
                .bankRspCode(new String(transactionLog.getBankRspCode()))
                .bankRspMessage(transactionLog.getBankRspMessage())
                .build();
        
        if (transactionLog.getUser() != null) {
            dto.setUserSeqNo(transactionLog.getUser().getUserSeqNo());
            dto.setUserName(transactionLog.getUser().getUserName());
        }
        
        if (transactionLog.getInsuranceContract() != null) {
            dto.setInsuId(transactionLog.getInsuranceContract().getInsuId());
            dto.setInsuNum(transactionLog.getInsuranceContract().getInsuNum());
            dto.setInsuNumMasked(transactionLog.getInsuranceContract().getInsuNumMasked());
        }
        
        return dto;
    }
}