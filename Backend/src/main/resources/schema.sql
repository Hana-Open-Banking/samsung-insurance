-- User table
CREATE TABLE IF NOT EXISTS user (
    user_seq_no VARCHAR(10) PRIMARY KEY,
    user_ci VARCHAR(100) NOT NULL UNIQUE,
    user_name VARCHAR(20) NOT NULL,
    user_reg_num VARCHAR(13) NOT NULL,
    gender CHAR(1) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    user_email VARCHAR(100) NOT NULL,
    user_info CHAR(8) NOT NULL,
    created_at DATETIME NOT NULL
);

-- Insurance Product table
CREATE TABLE IF NOT EXISTS insurance_product (
    product_id VARCHAR(10) PRIMARY KEY,
    product_type CHAR(2) NOT NULL,
    prod_name VARCHAR(100) NOT NULL,
    total_premium BIGINT NOT NULL
);

-- OAuth Client table
CREATE TABLE IF NOT EXISTS oauth_client (
    client_id VARCHAR(10) PRIMARY KEY,
    client_secret VARCHAR(100) NOT NULL,
    client_name VARCHAR(100) NOT NULL
);

-- Insurance Contract table
CREATE TABLE IF NOT EXISTS insurance_contract (
    insu_id VARCHAR(20) PRIMARY KEY,
    insu_num VARCHAR(20) NOT NULL,
    insu_num_masked VARCHAR(20) NOT NULL,
    user_seq_no VARCHAR(10) NOT NULL,
    product_id VARCHAR(10) NOT NULL,
    insu_type CHAR(2) NOT NULL,
    insu_status CHAR(2) NOT NULL,
    issue_date CHAR(8) NOT NULL,
    exp_date CHAR(8) NOT NULL,
    premium BIGINT NOT NULL,
    paid_premium BIGINT NOT NULL,
    special_yn CHAR(1) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_seq_no) REFERENCES user(user_seq_no),
    FOREIGN KEY (product_id) REFERENCES insurance_product(product_id)
);

-- OAuth Token table
CREATE TABLE IF NOT EXISTS oauth_token (
    access_token_id VARCHAR(100) PRIMARY KEY,
    client_id VARCHAR(10) NOT NULL,
    user_seq_no VARCHAR(10),
    expires_in INT NOT NULL,
    refresh_token VARCHAR(100) NOT NULL,
    scope VARCHAR(50),
    issued_at DATETIME NOT NULL,
    refreshed_at DATETIME,
    FOREIGN KEY (client_id) REFERENCES oauth_client(client_id),
    FOREIGN KEY (user_seq_no) REFERENCES user(user_seq_no)
);

-- Payment Info table
CREATE TABLE IF NOT EXISTS payment_info (
    payment_id VARCHAR(20) PRIMARY KEY,
    insu_id VARCHAR(20) NOT NULL,
    pay_due CHAR(2) NOT NULL,
    pay_cycle CHAR(2) NOT NULL,
    pay_date CHAR(2) NOT NULL,
    pay_end_date CHAR(8) NOT NULL,
    pay_amt DECIMAL(12) NOT NULL,
    pay_org_code CHAR(3) NOT NULL,
    pay_account_num VARCHAR(16) NOT NULL,
    pay_account_num_masked VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (insu_id) REFERENCES insurance_contract(insu_id)
);

-- API Transaction Log table
CREATE TABLE IF NOT EXISTS api_transaction_log (
    api_tran_id VARCHAR(40) PRIMARY KEY,
    api_tran_dtm CHAR(17) NOT NULL,
    rsp_code CHAR(5) NOT NULL,
    rsp_message VARCHAR(300) NOT NULL,
    bank_tran_id VARCHAR(20) NOT NULL,
    bank_tran_date CHAR(8) NOT NULL,
    bank_code_tran CHAR(3) NOT NULL,
    bank_rsp_code CHAR(3) NOT NULL,
    bank_rsp_message VARCHAR(100) NOT NULL,
    user_seq_no VARCHAR(10),
    insu_id VARCHAR(20),
    FOREIGN KEY (user_seq_no) REFERENCES user(user_seq_no),
    FOREIGN KEY (insu_id) REFERENCES insurance_contract(insu_id)
);

-- Index creation is now handled by Hibernate
-- ALTER TABLE insurance_contract DROP INDEX IF EXISTS idx_insu_num;
-- CREATE INDEX idx_insu_num ON insurance_contract(insu_num);

-- ALTER TABLE user DROP INDEX IF EXISTS idx_user_ci;
-- CREATE INDEX idx_user_ci ON user(user_ci);
