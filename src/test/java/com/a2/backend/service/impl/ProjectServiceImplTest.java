package com.a2.backend.service.impl;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.entity.Tag;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectServiceImplTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    String title = "Project title";
    String description = "Testing exception for existing title";
    List<String> links = Arrays.asList("link1", "link2");
    List<String> tags = Arrays.asList("tag1", "tag2");
    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    List<String> linksUpdate = Arrays.asList("link1", "link4");
    List<String> tagsUpdate = Arrays.asList("tag1", "tag4");

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
        userRepository.save(owner);

        assertTrue(projectService.getAllProjects().isEmpty());

        Project projectCreated = projectService.createProject(projectToCreate);

        val projects = projectService.getAllProjects();

        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        val project = projects.get(0);

        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
        assertEquals(tagService.findTagsByNames(projectToCreate.getTags()), project.getTags());
        assertEquals(projectToCreate.getLinks(), project.getLinks());
    }

    @Test
    void Test002_ProjectServiceWhenReceivesCreateProjectDTOWithExistingTitleShouldThrowException() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "Project title";
        String description2 = "Testing exception for existing title";

        ProjectCreateDTO projectToCreateWithRepeatedTitle =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description2)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
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
        userRepository.save(owner);

        assertTrue(projectService.getAllProjects().isEmpty());

        Project savedProject = projectService.createProject(projectToCreate);

        List<Project> allProjects = projectService.getAllProjects();

        assertEquals(1, allProjects.size());

        Project singleProject = allProjects.get(0);

        assertEquals(projectToCreate.getTitle(), singleProject.getTitle());
        assertEquals(projectToCreate.getDescription(), singleProject.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreate.getTags()), singleProject.getTags());
        assertEquals(projectToCreate.getLinks(), singleProject.getLinks());
    }

    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {
        userRepository.save(owner);

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
        userRepository.save(owner);

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
        userRepository.save(owner);

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
        userRepository.save(owner);

        Project project = projectService.createProject(projectToCreate);

        val projectToBeDisplayed = projectService.getProjectDetails(project.getId());

        assertEquals(project.getId(), projectToBeDisplayed.getId());
        assertEquals(projectToCreate.getOwner(), projectToBeDisplayed.getOwner());
        assertEquals(projectToCreate.getTitle(), projectToBeDisplayed.getTitle());
        assertEquals(projectToCreate.getDescription(), projectToBeDisplayed.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreate.getTags()),
                projectToBeDisplayed.getTags());
        assertEquals(projectToCreate.getLinks(), projectToBeDisplayed.getLinks());
    }

    @Test
    void
    Test011_GivenACreateProjectDTOWithExistingTitleButDifferentOwnerWhenCreatingProjectThenItIsCreated() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "Project title";
        String description2 = "Testing no exception for existing title but different owner";
        User owner2 =
                User.builder()
                        .nickname("nickname2")
                        .email("another@email.com")
                        .biography("another bio")
                        .password("anotherPassword")
                        .build();
        userRepository.save(owner2);
        List<String> links2 = Arrays.asList("link3", "link4");
        List<String> tags2 = Arrays.asList("tag3", "tag4");

        ProjectCreateDTO projectToCreateWithRepeatedTitle =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description2)
                        .links(links2)
                        .tags(tags2)
                        .owner(owner2)
                        .build();

        projectService.createProject(projectToCreateWithRepeatedTitle);

        val projects = projectService.getAllProjects();

        assertFalse(projects.isEmpty());
        assertEquals(2, projects.size());

        val project = projects.get(0);
        val project2 = projects.get(1);

        assertEquals(project.getTitle(), project2.getTitle());
        assertNotEquals(project.getOwner(), project2.getOwner());
    }

    @Test
    void Test012_GivenASingleExistingProjectWhenSearchedByTitleItShouldBeFound() {
        userRepository.save(owner);

        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Project")
                        .description(description)
                        .links(Arrays.asList("link3", "link4"))
                        .tags(Arrays.asList("tag5", "tag7"))
                        .owner(owner)
                        .build();

        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("ProjectProject")
                        .description(description)
                        .links(linksUpdate)
                        .tags(tagsUpdate)
                        .owner(owner)
                        .build();
        // Given
        assertTrue(projectService.getAllProjects().isEmpty());

        projectService.createProject(projectToCreate);
        projectService.createProject(secondProjectToCreate);
        projectService.createProject(thirdProjectToCreate);
        List<Project> projects = projectService.getProjectsByTitleSearch("Project");
        assertEquals(3, projects.size());
        assertEquals(projects.get(0).getTitle(), "Project title");
        assertEquals(projects.get(1).getTitle(), "ProjectProject");
        assertEquals(projects.get(2).getTitle(), "Not Project");
        // Then

    }

    @Test
    void Test013_GivenExistingTagsAndAValidProjectCreateDTOWhenCreatingProjectThenItIsCreated() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "A Non Existent Project title";
        List<String> tags2 = Arrays.asList("tag1", "tag4");

        ProjectCreateDTO projectToCreateWithRepeatedTag =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description)
                        .links(links)
                        .tags(tags2)
                        .owner(owner)
                        .build();

        val project = projectService.createProject(projectToCreateWithRepeatedTag);

        assertEquals(projectToCreateWithRepeatedTag.getTitle(), project.getTitle());
        assertEquals(projectToCreateWithRepeatedTag.getDescription(), project.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreateWithRepeatedTag.getTags()),
                project.getTags());
        assertEquals(projectToCreateWithRepeatedTag.getLinks(), project.getLinks());
    }

    @Test
    void
            Test014_ProjectServiceWhenReceivesValidProjectUpdateDTOAndIdShouldUpdateProjectAndDeleteUnusedTags() {
        Project createdProject = projectService.createProject(projectToCreate);

        List<Tag> tags = tagService.getAllTags();
        assertEquals(2, tags.size());
        assertEquals("tag1", createdProject.getTags().get(0).getName());
        assertEquals("tag2", createdProject.getTags().get(1).getName());

        Project updatedProject =
                projectService.updateProject(projectUpdateDTO, createdProject.getId());

        assertEquals(createdProject.getId(), updatedProject.getId());
        assertEquals(projectUpdateDTO.getTitle(), updatedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), updatedProject.getDescription());
        assertEquals(tagService.findTagsByNames(tagsUpdate), updatedProject.getTags());
        assertEquals(projectUpdateDTO.getLinks(), updatedProject.getLinks());

        List<Tag> updatedTags = tagService.getAllTags();
        assertEquals(2, updatedTags.size());
        assertEquals("tag1", updatedProject.getTags().get(0).getName());
        assertEquals("tag4", updatedProject.getTags().get(1).getName());
    }
}
