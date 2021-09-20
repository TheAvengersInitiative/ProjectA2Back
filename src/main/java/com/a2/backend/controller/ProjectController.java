package com.a2.backend.controller;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.service.ProjectService;
import java.util.Arrays;
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

    private final String linkRegex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

    @PostMapping
    public ResponseEntity<?> postNewProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        if (Arrays.stream(projectCreateDTO.getLinks())
                .anyMatch(t -> t == null || t.isEmpty() || !t.toLowerCase().matches(linkRegex))) {

            return ResponseEntity.badRequest().body("Invalid project link");
        }
        if (Arrays.stream(projectCreateDTO.getTags()).anyMatch(t -> t == null || t.isEmpty())) {
            return ResponseEntity.badRequest().body("Invalid project tag");
        }
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
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO, @PathVariable UUID id) {
        if (Arrays.stream(projectUpdateDTO.getLinks())
                .anyMatch(t -> t == null || t.isEmpty() || !t.toLowerCase().matches(linkRegex))) {
            return ResponseEntity.badRequest().body("Invalid project link");
        }
        if (Arrays.stream(projectUpdateDTO.getTags()).anyMatch(t -> t == null || t.isEmpty())) {
            return ResponseEntity.badRequest().body("Invalid project tag");
        }
        val updatedProject = projectService.updateProject(projectUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectDetails(@PathVariable UUID id) {
        val projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetails);
    }
}
