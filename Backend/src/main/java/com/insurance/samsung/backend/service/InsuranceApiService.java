package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.dto.InsuranceApiListRequest;
import com.insurance.samsung.backend.dto.InsuranceApiListResponse;
import com.insurance.samsung.backend.dto.InsuranceApiPaymentRequest;
import com.insurance.samsung.backend.dto.InsuranceApiPaymentResponse;
import com.insurance.samsung.backend.entity.InsuranceContract;
import com.insurance.samsung.backend.entity.PaymentInfo;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceApiService {
    
    private final InsuranceContractService contractService;
    private final PaymentInfoService paymentInfoService;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter API_TRAN_DTM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    @Transactional(readOnly = true)
    public InsuranceApiListResponse getInsuranceList(InsuranceApiListRequest request) {
        log.info("보험목록조회 서비스 시작 - userCi: {}", request.getUserCi());
        
        try {
            // 1. 사용자 CI로 사용자 조회
            Optional<User> userOpt = userRepository.findByUserCi(request.getUserCi());
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음 - userCi: {}", request.getUserCi());
                return createErrorResponse(request, "A003", "등록되지 않은 사용자입니다");
            }
            
            User user = userOpt.get();
            log.info("사용자 조회 완료 - userSeqNo: {}, userName: {}", user.getUserSeqNo(), user.getUserName());
            
            // 2. 사용자의 보험 계약 목록 조회
            List<InsuranceContract> contracts = contractService.getContractsByUser(user);
            log.info("보험 계약 조회 완료 - 계약 수: {}", contracts.size());
            
            // 3. 응답 생성
            List<InsuranceApiListResponse.InsuranceInfo> insuList = contracts.stream()
                    .map(this::convertToInsuranceInfo)
                    .collect(Collectors.toList());
            
            return InsuranceApiListResponse.builder()
                    .apiTranId(generateApiTranId())
                    .apiTranDtm(getCurrentDateTime())
                    .rspCode("A000")
                    .rspMessage("정상처리")
                    .bankTranId(request.getBankTranId())
                    .bankCodeStd(request.getBankCodeStd())
                    .memberBankCode(request.getMemberBankCode())
                    .userCi(request.getUserCi())
                    .insuCnt(String.valueOf(insuList.size()))
                    .insuList(insuList)
                    .build();
                    
        } catch (Exception e) {
            log.error("보험목록조회 처리 중 오류 발생", e);
            return createErrorResponse(request, "A001", "시스템 오류가 발생했습니다");
        }
    }
    
    @Transactional(readOnly = true)
    public InsuranceApiPaymentResponse getInsurancePayment(InsuranceApiPaymentRequest request) {
        log.info("보험납입정보조회 서비스 시작 - userCi: {}, insuNum: {}", request.getUserCi(), request.getInsuNum());
        
        try {
            // 1. 사용자 CI로 사용자 조회
            Optional<User> userOpt = userRepository.findByUserCi(request.getUserCi());
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음 - userCi: {}", request.getUserCi());
                return createPaymentErrorResponse(request, "A003", "등록되지 않은 사용자입니다");
            }
            
            User user = userOpt.get();
            log.info("사용자 조회 완료 - userSeqNo: {}, userName: {}", user.getUserSeqNo(), user.getUserName());
            
            // 2. 보험번호로 보험 계약 조회
            Optional<InsuranceContract> contractOpt = contractService.getContractByInsuNum(request.getInsuNum());
            if (contractOpt.isEmpty()) {
                log.warn("보험 계약을 찾을 수 없음 - insuNum: {}", request.getInsuNum());
                return createPaymentErrorResponse(request, "A004", "보험 계약을 찾을 수 없습니다");
            }
            
            InsuranceContract contract = contractOpt.get();
            
            // 3. 계약 소유자 확인
            if (!contract.getUser().getUserCi().equals(request.getUserCi())) {
                log.warn("보험 계약 소유자 불일치 - contractUserCi: {}, requestUserCi: {}", 
                        contract.getUser().getUserCi(), request.getUserCi());
                return createPaymentErrorResponse(request, "A005", "보험 계약 소유자가 일치하지 않습니다");
            }
            
            // 4. 납입 정보 조회
            Optional<PaymentInfo> paymentInfoOpt = paymentInfoService.getPaymentInfoByContractId(contract.getInsuId());
            if (paymentInfoOpt.isEmpty()) {
                log.warn("납입 정보를 찾을 수 없음 - insuId: {}", contract.getInsuId());
                return createPaymentErrorResponse(request, "A006", "납입 정보를 찾을 수 없습니다");
            }
            
            PaymentInfo paymentInfo = paymentInfoOpt.get();
            log.info("납입 정보 조회 완료 - paymentId: {}", paymentInfo.getPaymentId());
            
            // 5. 응답 생성
            return InsuranceApiPaymentResponse.builder()
                    .apiTranId(generateApiTranId())
                    .apiTranDtm(getCurrentDateTime())
                    .rspCode("A000")
                    .rspMessage("정상처리")
                    .bankTranId(request.getBankTranId())
                    .bankCodeStd(request.getBankCodeStd())
                    .memberBankCode(request.getMemberBankCode())
                    .userCi(request.getUserCi())
                    .insuNum(request.getInsuNum())
                    .payDue(new String(paymentInfo.getPayDue()))
                    .payCycle(new String(paymentInfo.getPayCycle()))
                    .payDate(new String(paymentInfo.getPayDate()))
                    .payEndDate(new String(paymentInfo.getPayEndDate()))
                    .payAmt(paymentInfo.getPayAmt().toString())
                    .payOrgCode(new String(paymentInfo.getPayOrgCode()))
                    .payAccountNumMasked(paymentInfo.getPayAccountNumMasked())
                    .build();
                    
        } catch (Exception e) {
            log.error("보험납입정보조회 처리 중 오류 발생", e);
            return createPaymentErrorResponse(request, "A001", "시스템 오류가 발생했습니다");
        }
    }
    
    /**
     * InsuranceContract를 InsuranceInfo로 변환
     */
    private InsuranceApiListResponse.InsuranceInfo convertToInsuranceInfo(InsuranceContract contract) {
        return InsuranceApiListResponse.InsuranceInfo.builder()
                .insuNum(contract.getInsuNum())
                .prodName(contract.getProduct().getProdName())
                .insuType(new String(contract.getInsuType()))
                .insuStatus(new String(contract.getInsuStatus()))
                .issueDate(contract.getIssueDate())
                .expDate(contract.getExpDate())
                .build();
    }
    
    /**
     * 보험목록조회 오류 응답 생성
     */
    private InsuranceApiListResponse createErrorResponse(InsuranceApiListRequest request, String rspCode, String rspMessage) {
        return InsuranceApiListResponse.builder()
                .apiTranId(generateApiTranId())
                .apiTranDtm(getCurrentDateTime())
                .rspCode(rspCode)
                .rspMessage(rspMessage)
                .bankTranId(request.getBankTranId())
                .bankCodeStd(request.getBankCodeStd())
                .memberBankCode(request.getMemberBankCode())
                .userCi(request.getUserCi())
                .insuCnt("0")
                .build();
    }
    
    /**
     * 보험납입정보조회 오류 응답 생성
     */
    private InsuranceApiPaymentResponse createPaymentErrorResponse(InsuranceApiPaymentRequest request, String rspCode, String rspMessage) {
        return InsuranceApiPaymentResponse.builder()
                .apiTranId(generateApiTranId())
                .apiTranDtm(getCurrentDateTime())
                .rspCode(rspCode)
                .rspMessage(rspMessage)
                .bankTranId(request.getBankTranId())
                .bankCodeStd(request.getBankCodeStd())
                .memberBankCode(request.getMemberBankCode())
                .userCi(request.getUserCi())
                .insuNum(request.getInsuNum())
                .build();
    }
    
    /**
     * API 거래 고유번호 생성
     */
    private String generateApiTranId() {
        return "SMSNG" + System.currentTimeMillis();
    }
    
    /**
     * 현재 일시 문자열 생성
     */
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(API_TRAN_DTM_FORMAT);
    }
} 