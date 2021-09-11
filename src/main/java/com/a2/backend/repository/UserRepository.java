package com.a2.backend.repository;

import com.a2.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);
}
