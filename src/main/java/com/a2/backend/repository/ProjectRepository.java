package com.a2.backend.repository;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByTitle(String title);

    void deleteByOwner(User owner);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.tags t WHERE UPPER(t.name) LIKE CONCAT('%',UPPER(name),'%')")
    List<Project> findProjectsByTagName(String name);

    @Query(
            "SELECT DISTINCT p FROM Project p JOIN p.languages l WHERE UPPER(l.name) LIKE CONCAT('%',UPPER(name),'%')")
    List<Project> findProjectsByLanguageName(String name);

    List<Project> findByTitleContaining(String title);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.links l  WHERE l LIKE %?1%")
    List<Project> findProjectsByLink(String links);
}
