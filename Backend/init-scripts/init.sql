-- User table
CREATE TABLE IF NOT EXISTS user (
    user_seq_no VARCHAR(10) PRIMARY KEY,
    user_ci VARCHAR(100) NOT NULL UNIQUE,
    user_name VARCHAR(20) NOT NULL,
    user_reg_num VARCHAR(13) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    user_info VARCHAR(8) NOT NULL,
    created_at DATETIME NOT NULL
);

-- Insurance Product table
CREATE TABLE IF NOT EXISTS insurance_product (
    product_id VARCHAR(10) PRIMARY KEY,
    product_type VARCHAR(2) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    total_premium BIGINT NOT NULL
);

-- Insurance Contract table
CREATE TABLE IF NOT EXISTS insurance_contract (
    insu_id VARCHAR(20) PRIMARY KEY,
    insu_num VARCHAR(20) NOT NULL,
    user_seq_no VARCHAR(10) NOT NULL,
    product_id VARCHAR(10) NOT NULL,
    insu_type VARCHAR(2) NOT NULL,
    insu_status VARCHAR(2) NOT NULL,
    issue_date CHAR(8) NOT NULL,
    exp_date CHAR(8) NOT NULL,
    premium BIGINT NOT NULL,
    paid_premium BIGINT NOT NULL,
    special_yn VARCHAR(1) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_seq_no) REFERENCES user(user_seq_no),
    FOREIGN KEY (product_id) REFERENCES insurance_product(product_id)
);

-- OAuth Client table
CREATE TABLE IF NOT EXISTS oauth_client (
    client_id VARCHAR(10) PRIMARY KEY,
    client_secret VARCHAR(100) NOT NULL,
    client_name VARCHAR(100) NOT NULL
);

-- OAuth Token table
CREATE TABLE IF NOT EXISTS oauth_token (
    access_token_id VARCHAR(100) PRIMARY KEY,
    client_id VARCHAR(10) NOT NULL,
    expires_in INT NOT NULL,
    refresh_token VARCHAR(100) NOT NULL,
    scope VARCHAR(50),
    issued_at DATETIME NOT NULL,
    refreshed_at DATETIME,
    FOREIGN KEY (client_id) REFERENCES oauth_client(client_id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_user_ci ON user(user_ci);
CREATE INDEX IF NOT EXISTS idx_insu_num ON insurance_contract(insu_num);

-- Insurance Product initial data
INSERT INTO insurance_product (product_id, product_type, product_name, total_premium) VALUES
('PROD001', '01', '실손의료비보험', 1440000),
('PROD002', '02', '종합보험', 2400000),
('PROD003', '03', '자동차보험', 1800000);

-- OAuth Client initial data
INSERT INTO oauth_client (client_id, client_secret, client_name) VALUES
('CLIENT001', 'secret123', '오픈뱅킹테스트'),
('CLIENT002', 'secret456', '금융결제원');

-- Test User data
INSERT INTO user (user_seq_no, user_ci, user_name, user_reg_num, gender, password, phone_number, user_email, user_info, created_at) VALUES
('1000000001', 'CI_12345678901234567890', '홍길동', '9001011234567', 'M', 'cGFzc3dvcmQxMjMh', '010-1234-5678', 'hong@test.com', '19900101', NOW()),
('1000000002', 'CI_09876543210987654321', '김영희', '9501011234567', 'F', 'cGFzc3dvcmQ0NTYh', '010-9876-5432', 'kim@test.com', '19950101', NOW());

-- Test Insurance Contract data
INSERT INTO insurance_contract (insu_id, insu_num, user_seq_no, product_id, insu_type, insu_status, issue_date, exp_date, premium, paid_premium, special_yn, created_at) VALUES
('INSU20240001', '12345678901234567890', '1000000001', 'PROD001', '01', '01', '20240101', '20340101', 120000, 1200000, 'Y', NOW()),
('INSU20240002', '09876543210987654321', '1000000002', 'PROD002', '02', '01', '20240201', '20340201', 200000, 400000, 'N', NOW());