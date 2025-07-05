package com.insurance.samsung.backend.repository;

import com.insurance.samsung.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserCi(String userCi);

    Optional<User> findByUserEmailIgnoreCase(String userEmail);

    boolean existsByUserCi(String userCi);
}
