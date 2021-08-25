package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
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
    void ProjectWithThatTitleExistsExceptionTest() {
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

}