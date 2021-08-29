package com.a2.backend.controller;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.service.ProjectService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> postNewProject(
            @Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        val createdProject = projectService.createProject(projectCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        val projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO, @PathVariable UUID id) {
        val updatedProject = projectService.updateProject(projectUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectDetails(@PathVariable UUID id) {
        val projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetails);
    }
}
