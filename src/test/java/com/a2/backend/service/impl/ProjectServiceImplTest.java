package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;

import com.a2.backend.exception.ProjectWithThatIdDoesntExistException;

import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.ProjectService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;



import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectServiceImplTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    String title = "Project title";
    String  description = "Testing exception for existing title";
    String owner = "Owner´s name";

    ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
            .title(title)
            .description(description)
            .owner(owner)
            .build();


    @Test

    void Test001_ProjectServiceWhenReceivesValidCreateProjectDTOShouldCreateProject() {

        assertTrue(projectRepository.findAll().isEmpty());

        Project projectCreated = projectService.createProject(projectToCreate);

        val projects = projectRepository.findAll();

        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        val project = projects.get(0);
        assertEquals(project, projectCreated);
    }

    void createProjectTest(){

        assertTrue(projectRepository.findAll().isEmpty());

        Project projectCreated = projectService.createProject(projectToCreate);

        val projects = projectRepository.findAll();

        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        val project = projects.get(0);
        assertEquals(project, projectCreated);


    }

    @Test

    void Test002_ProjectServiceWhenReceivesCreateProjectDTOWithExistingTitleShouldThrowException() {
      projectService.createProject(projectToCreate);

      String title2 = "Project title";
      String  description2 = "Testing exception for existing title";
      String owner2 = "Owner´s name";

      ProjectCreateDTO projectToCreateWithRepeatedTitle = ProjectCreateDTO.builder()
                                                  .title(title2)
                                                  .description(description2)
                                                  .owner(owner2)
                                                  .build();

       assertThrows(ProjectWithThatTitleExistsException.class, () -> {
          projectService.createProject(projectToCreateWithRepeatedTitle);
       });
    }

    @Test
    void Test003_ProjectServiceWhenReceiveCreateProjectDTOWithNullTitleShouldThrowNullPointerException() {

        assertThrows(NullPointerException.class, () -> {
            ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                    .description(description)
                    .owner(owner)
                    .build();

    });
    }


    @Test

    void Test004_ProjectListWithNoSavedProjectsShouldBeEmpty() {
        assertTrue(projectService.getAllProjects().isEmpty());
    }

    @Test
    void Test005_ProjectListWithSavedProjectsShouldContainProjects() {
        assertTrue(projectService.getAllProjects().isEmpty());

        Project savedProject = projectService.createProject(projectToCreate);

        List<Project> allProjects = projectService.getAllProjects();

        assertEquals(1, allProjects.size());

        Project singleProject = allProjects.get(0);
        assertEquals(savedProject, singleProject);
    }


    void ProjectWithThatIdDoesntExistExceptionTest() {
        Project project= projectService.createProject(projectToCreate);
        projectService.deleteProject(project.getId());
        assertThrows(ProjectWithThatIdDoesntExistException.class, () -> {
            projectService.deleteProject(project.getId());
        });
    }

}

