package com.a2.backend.repository;

import com.a2.backend.entity.Discussion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {
    @Query("SELECT d FROM Discussion d WHERE d.title LIKE ?2 AND d.project.id=?1")
    Discussion findByProjectIdAndTitle(UUID id, String title);
}
