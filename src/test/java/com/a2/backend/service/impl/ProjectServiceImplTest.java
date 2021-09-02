package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
import com.a2.backend.exception.ProjectNotFoundException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectServiceImplTest {

    @Autowired private ProjectService projectService;

    @Autowired private ProjectRepository projectRepository;

    String title = "Project title";
    String description = "Testing exception for existing title";
    String[] links = {"link1", "link2"};
    String[] tags = {"tag1", "tag2"};
    String owner = "Owner´s name";

    String[] uLinks = {"link1", "link4"};
    String[] uTags = {"tag4", "tag2"};
    List<String> linksUpdate = new ArrayList<>(Arrays.asList(uLinks));
    List<String> tagsUpdate = new ArrayList<>(Arrays.asList(uTags));

    ProjectCreateDTO projectToCreate =
            ProjectCreateDTO.builder()
                    .title(title)
                    .description(description)
                    .links(links)
                    .tags(tags)
                    .owner(owner)
                    .build();
    ProjectUpdateDTO projectUpdateDTO =
            ProjectUpdateDTO.builder()
                    .title("new title")
                    .links(linksUpdate)
                    .tags(tagsUpdate)
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

        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
        assertEquals(Arrays.asList(projectToCreate.getTags()), project.getTags());
        assertEquals(Arrays.asList(projectToCreate.getLinks()), project.getLinks());
    }

    @Test
    void Test002_ProjectServiceWhenReceivesCreateProjectDTOWithExistingTitleShouldThrowException() {
        projectService.createProject(projectToCreate);

        String title2 = "Project title";
        String description2 = "Testing exception for existing title";
        String owner2 = "Owner´s name";

        ProjectCreateDTO projectToCreateWithRepeatedTitle =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description2)
                        .links(links)
                        .tags(tags)
                        .owner(owner2)
                        .build();

        assertThrows(
                ProjectWithThatTitleExistsException.class,
                () -> projectService.createProject(projectToCreateWithRepeatedTitle));
    }

    @Test
    void
            Test003_ProjectServiceWhenReceiveCreateProjectDTOWithNullTitleShouldThrowNullPointerException() {

        assertThrows(
                NullPointerException.class,
                () -> {
                    ProjectCreateDTO projectToCreate =
                            ProjectCreateDTO.builder()
                                    .description(description)
                                    .links(links)
                                    .tags(tags)
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

        assertEquals(projectToCreate.getTitle(), singleProject.getTitle());
        assertEquals(projectToCreate.getDescription(), singleProject.getDescription());
        assertEquals(Arrays.asList(projectToCreate.getTags()), singleProject.getTags());
        assertEquals(Arrays.asList(projectToCreate.getLinks()), singleProject.getLinks());
    }

    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {

        // Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project = projectService.createProject(projectToCreate);
        List<Project> allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        // When
        projectService.deleteProject(project.getId());

        // Then
        assertTrue(projectService.getAllProjects().isEmpty());
    }

    /** Given non existent id when projectService.updateProject then throw NotFoundException */
    @Test
    void
            Test007_ProjectServiceWhenRecievesNonExistentProjectIDShouldThrowProjectNotFoundException() {
        UUID nonexistentID = UUID.randomUUID();
        assertTrue(projectRepository.findById(nonexistentID).isEmpty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.updateProject(projectUpdateDTO, nonexistentID));
    }

    /**
     * Given right project id & updateProjectDTO when projectService.updateProject() then update
     * project with that id
     */
    @Test
    void Test008_ProjectServiceWhenReceivesValidProjectUpdateDTOAndIdShouldUpdateProject() {
        val projectToModify = projectService.createProject(projectToCreate);

        assertEquals(projectToCreate.getTitle(), projectToModify.getTitle());
        assertEquals(projectToCreate.getDescription(), projectToModify.getDescription());

        val modifiedProject =
                projectService.updateProject(projectUpdateDTO, projectToModify.getId());

        assertEquals(projectToModify.getId(), modifiedProject.getId());
        assertEquals(projectUpdateDTO.getTitle(), modifiedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), modifiedProject.getDescription());
    }

    @Test
    void Test009_GivenASingleExistingProjectWhenDeletedTwiceThenExceptionShouldBeThrown() {

        // Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project = projectService.createProject(projectToCreate);
        List<Project> allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        // When
        projectService.deleteProject(project.getId());

        // Then
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.deleteProject(project.getId()));
    }

    @Test
    void Test010_GivenValidProjectIDWhenAskedForProjectThenReturnProject() {
        Project project = projectService.createProject(projectToCreate);

        val projectToBeDisplayed = projectService.getProjectDetails(project.getId());

        assertEquals(project.getId(), projectToBeDisplayed.getId());
        assertEquals(projectToCreate.getOwner(), projectToBeDisplayed.getOwner());
        assertEquals(projectToCreate.getTitle(), projectToBeDisplayed.getTitle());
        assertEquals(projectToCreate.getDescription(), projectToBeDisplayed.getDescription());
        assertEquals(Arrays.asList(projectToCreate.getTags()), projectToBeDisplayed.getTags());
        assertEquals(Arrays.asList(projectToCreate.getLinks()), projectToBeDisplayed.getLinks());
    }
}
