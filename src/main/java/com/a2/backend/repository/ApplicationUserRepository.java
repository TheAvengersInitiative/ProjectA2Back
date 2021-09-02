package com.a2.backend.repository;

import com.a2.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, UUID> {

    Optional<ApplicationUser> findByNickname(String nickname);

    Optional<ApplicationUser> findByEmail(String email);
}
