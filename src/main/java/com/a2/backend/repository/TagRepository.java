package com.a2.backend.repository;

import com.a2.backend.entity.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByName(String name);

    @Query("SELECT DISTINCT name FROM Tag t WHERE UPPER(t.name)  LIKE %?1%")
    List<String> findTagName(String tagname);
}
