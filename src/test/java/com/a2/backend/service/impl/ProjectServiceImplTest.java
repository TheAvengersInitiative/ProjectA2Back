package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;



import com.a2.backend.exception.ProjectNotFoundException;


import com.a2.backend.exception.ProjectWithThatIdDoesntExistException;

import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
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
    String description = "Testing exception for existing title";
    String owner = "Owner´s name";

    ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
            .title(title)
            .description(description)
            .owner(owner)
            .build();
    ProjectUpdateDTO projectUpdateDTO = ProjectUpdateDTO.builder()
            .title("new title")
            .description("new description")
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


    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {

        //Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project= projectService.createProject(projectToCreate);
        List<Project> allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        //When
        projectService.deleteProject(project.getId());

        //Then
        assertTrue(projectService.getAllProjects().isEmpty());
    }



    /**
     * Given non existent id
     * when projectService.updateProject
     * then throw NotFoundException
     */
    @Test
    void Test_001_ProjectServiceWhenRecievesNonExistentProjectIDShouldThrowProjectNotFoundException() {
        String nonexistentID = "id_001";
        assertTrue(projectRepository.findById(nonexistentID).isEmpty());

        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.updateProject(projectUpdateDTO, nonexistentID);
        });
    }

    /**
     * Given right project id & updateProjectDTO
     * when projectService.updateProject()
     * then update project with that id
     */
    @Test
    void Test_002_UpdateProject() {
        val projectToModify = projectService.createProject(projectToCreate);

        assertEquals("Project title", projectToModify.getTitle());
        assertEquals("Testing exception for existing title", projectToModify.getDescription());

       val modifiedProject=  projectService.updateProject(projectUpdateDTO, projectToModify.getId());

//        val my_Updated_Projects = projectService.getAllProjects();
//        assertFalse(my_Updated_Projects.isEmpty());
//        assertEquals(1, my_Updated_Projects.size());
//
//        val myUpdatedProject = my_Updated_Projects.get(0);
//        assertEquals(myUpdatedProject.getTitle() , projectToModify.getTitle());
        assertEquals("new title" , modifiedProject.getTitle());
        assertEquals("new description" , modifiedProject.getDescription());
    }




    @Test
    void Test007_GivenASingleExistingProjectWhenDeletedTwiceThenExceptionShouldBeThrown() {

        //Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project= projectService.createProject(projectToCreate);
        List<Project> allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        //When
        projectService.deleteProject(project.getId());

        //Then
        assertThrows(ProjectWithThatIdDoesntExistException.class, () -> {
            projectService.deleteProject(project.getId());
        });
    }



    @Test
    void Test_003_GivenValidProjectIDWhenAskedForProjectThenReturnProject(){
        Project project= projectService.createProject(projectToCreate);

        val projectToBeDisplayed = projectService.getProjectDetails(project.getId());

        assertEquals(project.getId() , projectToBeDisplayed.getId());
        assertEquals(project.getOwner() , projectToBeDisplayed.getOwner());
        assertEquals(project.getTitle() , projectToBeDisplayed.getTitle());
        assertEquals(project.getDescription() , projectToBeDisplayed.getDescription());
        assertEquals(project.getLinks() , projectToBeDisplayed.getLinks());
        assertEquals(project.getTags() , projectToBeDisplayed.getTags());
    }
}



