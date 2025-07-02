package com.insurance.samsung.backend.service;

import com.insurance.samsung.backend.entity.User;
import com.insurance.samsung.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserBySeqNo(String userSeqNo) {
        return userRepository.findById(userSeqNo);
    }

    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String userEmail, String password) {
        Optional<User> userOpt = userRepository.findByUserEmail(userEmail);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String encodedPassword = encodePassword(password);

            if (encodedPassword.equals(user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUserCi(user.getUserCi())) {
            throw new RuntimeException("User with this CI already exists");
        }

        // Generate a unique user sequence number (in a real system, this might be more sophisticated)
        String userSeqNo = generateUserSeqNo();
        user.setUserSeqNo(userSeqNo);

        // Encrypt the password
        user.setPassword(encodePassword(user.getPassword()));

        // Set creation timestamp
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    private String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            System.out.println("이 값 넣기");
            System.out.println(Base64.getEncoder().encodeToString(hash));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    private String generateUserSeqNo() {
        // In a real system, this would be more sophisticated
        // For now, we'll just count the number of users and add 1000000001
        long count = userRepository.count();
        return String.format("%010d", 1000000001L + count);
    }
}
