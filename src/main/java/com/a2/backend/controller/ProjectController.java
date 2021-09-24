package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectSearchDTO;
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
import org.springframework.security.access.annotation.Secured;
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

    @Secured({SecurityConstants.USER_ROLE})
    @PostMapping
    public ResponseEntity<?> postNewProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        val createdProject = projectService.createProject(projectCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        val projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }




    @Secured({SecurityConstants.USER_ROLE})
    @PostMapping("/search")
    public ResponseEntity<List<Project>> getProjectsByNameSearch(
            @Valid @RequestBody ProjectSearchDTO projectSearchDTO) {
        val projects = projectService.searchProjecsByFilter(projectSearchDTO);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @Valid @RequestBody ProjectUpdateDTO projectUpdateDTO, @PathVariable UUID id) {
        val updatedProject = projectService.updateProject(projectUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectDetails(@PathVariable UUID id) {
        val projectDetails = projectService.getProjectDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetails);
    }

    @Secured({SecurityConstants.USER_ROLE})
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
