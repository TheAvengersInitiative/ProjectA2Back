package com.a2.backend.repository;

import com.a2.backend.entity.Language;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LanguageRepository extends JpaRepository<Language, UUID> {

    Optional<Language> findByName(String name);

    @Query(
            "SELECT DISTINCT name FROM Language l WHERE UPPER(l.name) LIKE CONCAT('%',UPPER(name),'%')")
    List<String> findLanguageName(String name);
}
