-- Insurance Product initial data
INSERT INTO insurance_product (product_id, product_type, prod_name, total_premium) VALUES
('PROD001', '01', '실손의료비보험', 1440000),
('PROD002', '02', '종합보험', 2400000),
('PROD003', '03', '자동차보험', 1800000);

-- OAuth Client initial data
INSERT INTO oauth_client (client_id, client_secret, client_name) VALUES
('CLIENT001', 'secret123', '오픈뱅킹테스트'),
('CLIENT002', 'secret456', '금융결제원');

-- Test User data
INSERT INTO user (user_seq_no, user_ci, user_name, user_reg_num, gender, password, phone_number, user_email, user_info, created_at) VALUES
('1000000001', 'CI_12345678901234567890', '홍길동', '9001011234567', 'M', 'V1GkR4JZSBnky4qifCydh6Qgr4K8aloFvH8Zw7sARSs=', '010-1234-5678', 'hong@test.com', '19900101', NOW()),
('1000000002', 'CI_09876543210987654321', '김영희', '9501011234567', 'F', 'V1GkR4JZSBnky4qifCydh6Qgr4K8aloFvH8Zw7sARSs=', '010-9876-5432', 'kim@test.com', '19950101', NOW());

-- Test Insurance Contract data
INSERT INTO insurance_contract (insu_id, insu_num, insu_num_masked, user_seq_no, product_id, insu_type, insu_status, issue_date, exp_date, premium, paid_premium, special_yn, created_at) VALUES
('INSU20240001', '12345678901234567890', '123**************890', '1000000001', 'PROD001', '01', '01', '20240101', '20340101', 120000, 1200000, 'Y', NOW()),
('INSU20240002', '09876543210987654321', '098**************321', '1000000002', 'PROD002', '02', '01', '20240201', '20340201', 200000, 400000, 'N', NOW());

-- Payment Info sample data
INSERT INTO payment_info (payment_id, insu_id, pay_due, pay_cycle, pay_date, pay_end_date, pay_amt, pay_org_code, pay_account_num, pay_account_num_masked, created_at, updated_at) VALUES
('PAY20240001', 'INSU20240001', '01', '1M', '15', '20340101', 120000.00, '001', '1234567890123456', '123*********456', NOW(), NOW()),
('PAY20240002', 'INSU20240002', '02', '6M', '25', '20340201', 200000.00, '002', '9876543210987654', '987*********654', NOW(), NOW());

-- API Transaction Log sample data
INSERT INTO api_transaction_log (api_tran_id, api_tran_dtm, rsp_code, rsp_message, bank_tran_id, bank_tran_date, bank_code_tran, bank_rsp_code, bank_rsp_message, user_seq_no, insu_id) VALUES
('API20240001000000000001', '20240101120000000', '00000', '정상처리 되었습니다', 'BANK20240001000001', '20240101', '001', '000', '정상', '1000000001', 'INSU20240001'),
('API20240001000000000002', '20240102130000000', '00000', '정상처리 되었습니다', 'BANK20240001000002', '20240102', '002', '000', '정상', '1000000002', 'INSU20240002');

-- INSU20240001 (홍길동, 월 12만원 보험료)에 대한 납입 정보
INSERT INTO payment_info (
    payment_id,
    insu_id,
    pay_amt,
    pay_cycle,
    pay_date,
    pay_due,
    pay_end_date,
    pay_org_code,
    pay_account_num,
    pay_account_num_masked,
    created_at,
    updated_at
) VALUES (
             'PAY20240001',           -- payment_id: 납입정보 고유ID
             'INSU20240001',          -- insu_id: 보험계약 ID
             120000,                  -- pay_amt: 납입금액 (월 12만원)
             '01',                    -- pay_cycle: 납입주기 (01:월납, 02:연납, 03:일시납)
             '25',                    -- pay_date: 납입일 (매월 25일)
             '05',                    -- pay_due: 납입유예일 (5일)
             '20340101',              -- pay_end_date: 납입종료일 (보험만료일과 동일)
             '449',                   -- pay_org_code: 납입기관코드 (은행코드)
             '1234567890123456',      -- pay_account_num: 납입계좌번호
             '1234**********3456',    -- pay_account_num_masked: 마스킹된 계좌번호
             NOW(),                   -- created_at: 생성일시
             NOW()                    -- updated_at: 수정일시
         );

-- INSU20240002 (김영희, 월 20만원 보험료)에 대한 납입 정보
INSERT INTO payment_info (
    payment_id,
    insu_id,
    pay_amt,
    pay_cycle,
    pay_date,
    pay_due,
    pay_end_date,
    pay_org_code,
    pay_account_num,
    pay_account_num_masked,
    created_at,
    updated_at
) VALUES (
             'PAY20240002',           -- payment_id: 납입정보 고유ID
             'INSU20240002',          -- insu_id: 보험계약 ID
             200000,                  -- pay_amt: 납입금액 (월 20만원)
             '01',                    -- pay_cycle: 납입주기 (01:월납)
             '15',                    -- pay_date: 납입일 (매월 15일)
             '03',                    -- pay_due: 납입유예일 (3일)
             '20340201',              -- pay_end_date: 납입종료일 (보험만료일과 동일)
             '449',                   -- pay_org_code: 납입기관코드 (은행코드)
             '9876543210987654',      -- pay_account_num: 납입계좌번호
             '9876**********7654',    -- pay_account_num_masked: 마스킹된 계좌번호
             NOW(),                   -- created_at: 생성일시
             NOW()                    -- updated_at: 수정일시
         );

-- 데이터 확인 쿼리
SELECT
    pi.payment_id,
    pi.insu_id,
    ic.insu_num,
    pi.pay_amt,
    pi.pay_cycle,
    pi.pay_date,
    pi.pay_due,
    pi.pay_end_date,
    pi.pay_org_code,
    pi.pay_account_num_masked
FROM payment_info pi
         JOIN insurance_contract ic ON pi.insu_id = ic.insu_id
ORDER BY pi.payment_id;