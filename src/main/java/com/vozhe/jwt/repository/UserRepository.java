package com.vozhe.jwt.repository;

import com.vozhe.jwt.models.User;
import com.vozhe.jwt.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String name);
}
