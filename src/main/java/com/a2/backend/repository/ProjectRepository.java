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

<<<<<<< HEAD
    void deleteByOwner(User owner);

    List<Project> findByTitleStartsWithIgnoreCaseOrderByTitleAsc(String pattern);

=======
>>>>>>> e5755c3 (fix de endpoint y paginacion)
    List<Project> findByTitleContainingIgnoreCaseOrderByTitleAsc(String pattern);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.tags t  WHERE t.name LIKE %?1%")
    List<Project> findProjectsByTagName(String tagName);
}
