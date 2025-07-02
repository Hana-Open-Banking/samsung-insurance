package com.insurance.samsung.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userSeqNo;
    private String userCi;
    private String userName;
    private String userRegNum;
    private String gender;
    private String password;
    private String phoneNumber;
    private String userEmail;
    private String userInfo;
    private LocalDateTime createdAt;
}