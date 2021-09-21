package com.a2.backend.repository;

import com.a2.backend.entity.Language;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, UUID> {

    Optional<Language> findByName(String name);
}
