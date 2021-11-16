package com.a2.backend.repository;

import com.a2.backend.entity.Discussion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {
    @Query("SELECT d FROM Discussion d WHERE d.title LIKE ?2 AND d.project.id=?1")
    Discussion findByProjectIdAndTitle(UUID id, String title);

    @Query("SELECT DISTINCT d FROM Discussion d JOIN d.comments c WHERE UPPER(c.id) = ?1 ")
    Optional<Discussion> findDiscussionByCommentId(UUID commentId);

    Optional<Discussion> findByTitle(String title);

    @Query("SELECT DISTINCT d FROM Discussion d JOIN d.forumTags t WHERE UPPER(t.name) = ?1 ")
    List<Discussion> findDiscussionsByTagName(String name);
}
