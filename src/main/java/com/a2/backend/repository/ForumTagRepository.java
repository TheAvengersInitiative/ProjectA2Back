package com.a2.backend.repository;

import com.a2.backend.entity.ForumTag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ForumTagRepository extends JpaRepository<ForumTag, UUID> {

    Optional<ForumTag> findByName(String name);

    @Query("SELECT DISTINCT name FROM ForumTag t WHERE UPPER(t.name)  LIKE %?1%")
    List<String> findForumTagName(String name);
}
