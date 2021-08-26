package com.a2.backend.controller;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.service.ProjectService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping
    public ResponseEntity<Project> postNewProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        val createdProject = projectService.createProject(projectCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }


    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        val projects = projectService.getAllProjects();
        return ResponseEntity.status(HttpStatus.OK).body(projects);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);

    }

    @PutMapping
    public ResponseEntity<Project> updateProject(@RequestBody ProjectUpdateDTO projectUpdateDTO , @PathVariable String projectToBeUpdatedID){
        val updatedProject = projectService.updateProject(projectUpdateDTO,projectToBeUpdatedID);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedProject);
    }
}
