package com.a2.backend.repository;

import com.a2.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findByTitle(String title);
}
