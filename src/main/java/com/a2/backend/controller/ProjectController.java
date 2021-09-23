package com.a2.backend.controller;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final TagService tagService;

    public ProjectController(ProjectService projectService, TagService tagService) {
        this.projectService = projectService;
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<?> postNewProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        val createdProject = projectService.createProject(projectCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        val projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Project>> getProjectsByNameSearch(
            @RequestParam(name = "name") String pattern,
            @RequestParam(name = "page") Integer pageNo) {
        val projects = projectService.getProjectsByTitleSearch(pattern, pageNo);

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
        val updatedProject = projectService.updateProject(projectUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectDetails(@PathVariable UUID id) {
        val projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetails);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getValidLanguages() {
        val validLanguages = projectService.getValidLanguageNames();
        return ResponseEntity.status(HttpStatus.OK).body(validLanguages);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getTags() {
        return ResponseEntity.ok(
                tagService.getAllTags().stream().map(Tag::getName).collect(Collectors.toList()));
    }
}
