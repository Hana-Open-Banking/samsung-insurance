package com.insurance.samsung.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_user_ci", columnList = "user_ci", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_seq_no", length = 10)
    private String userSeqNo;

    @Column(name = "user_ci", length = 100, nullable = false, unique = true)
    private String userCi;

    @Column(name = "user_name", length = 20, nullable = false)
    private String userName;

    @Column(name = "user_reg_num", length = 13, nullable = false)
    private String userRegNum;

    @Column(name = "gender", length = 1, nullable = false)
    private char gender;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "user_email", length = 100, nullable = false)
    private String userEmail;

    @Column(name = "user_info", length = 8, nullable = false)
    private char[] userInfo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
