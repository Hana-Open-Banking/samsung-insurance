package com.insurance.samsung.backend.controller;

import com.insurance.samsung.backend.dto.UserDto;
import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.service.OAuthService;
import com.insurance.samsung.backend.service.UserService;
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
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final OAuthService oauthService;

    @Autowired
    public UserController(UserService userService, OAuthService oauthService) {
        this.userService = userService;
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

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        List<User> users = userService.getAllUsers();
        System.out.println(users.size());
        List<UserDto> userDtos = users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{userSeqNo}")
    public ResponseEntity<?> getUserBySeqNo(
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

        Optional<User> userOpt = userService.getUserBySeqNo(userSeqNo);
        return userOpt.map(user -> ResponseEntity.ok(convertToDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        // Get user by token
        Optional<User> userOpt = oauthService.getUserByToken(token);

        if (userOpt.isEmpty()) {
            // Check if token is valid but no user is associated
            if (oauthService.validateToken(token).isPresent()) {
                return createErrorResponse("USER_NOT_FOUND", "토큰에 연결된 사용자가 없습니다", HttpStatus.NOT_FOUND);
            }
            // Token is invalid
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(convertToDto(userOpt.get()));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            @RequestBody UserDto userDto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Validate token
        String token = extractToken(authHeader);
        if (token == null) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 없습니다", HttpStatus.UNAUTHORIZED);
        }

        if (oauthService.validateToken(token).isEmpty()) {
            return createErrorResponse("INVALID_TOKEN", "액세스 토큰이 유효하지 않습니다", HttpStatus.UNAUTHORIZED);
        }

        try {
            User user = convertToEntity(userDto);
            User savedUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedUser));
        } catch (RuntimeException e) {
            // Handle duplicate CI error
            if (e.getMessage().contains("CI already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            throw e;
        }
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .userSeqNo(user.getUserSeqNo())
                .userName(user.getUserName())
                .gender(String.valueOf(user.getGender()))
                .phoneNumber(user.getPhoneNumber())
                .userEmail(user.getUserEmail())
                .userInfo(new String(user.getUserInfo()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User convertToEntity(UserDto userDto) {
        return User.builder()
                .userCi(userDto.getUserCi())
                .userName(userDto.getUserName())
                .userRegNum(userDto.getUserRegNum())
                .gender(userDto.getGender().charAt(0))
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .userEmail(userDto.getUserEmail())
                .userInfo(userDto.getUserInfo().toCharArray())
                .build();
    }
}
