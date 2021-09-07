package com.a2.backend.repository;

import com.a2.backend.entity.ConfirmationToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, UUID> {

    public Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

    public Optional<ConfirmationToken> findByUserEntity(UUID userID);
}
