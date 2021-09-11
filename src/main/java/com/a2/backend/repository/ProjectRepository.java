package com.a2.backend.repository;

import com.a2.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Optional<Project> findByTitle(String title);

    List<Project> findByTitleStartsWithIgnoreCaseOrderByTitleAsc(String pattern);

    List<Project> findByTitleContainingIgnoreCaseOrderByTitleAsc(String pattern);
}
