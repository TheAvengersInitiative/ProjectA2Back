package com.a2.backend.repository;

import com.a2.backend.entity.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findByTitle(String title);
}
