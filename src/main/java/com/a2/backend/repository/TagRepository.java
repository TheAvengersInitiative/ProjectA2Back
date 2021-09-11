package com.a2.backend.repository;

import com.a2.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByName(String name);
}
