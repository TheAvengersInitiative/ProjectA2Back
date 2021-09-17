package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.entity.User;
import com.a2.backend.utils.SearchUtils.ProjectSpecification;
import com.a2.backend.utils.SearchUtils.SearchCriteria;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectRepositoryTest {

    @Autowired private ProjectRepository projectRepository;

    @Autowired private UserRepository userRepository;

    String title = "New project";
    String description = "Testing project repository";
    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    Project project = Project.builder().title(title).description(description).owner(owner).build();

    @Test
    void Test001_ProjectRepositoryShouldSaveProjects() {
        userRepository.save(owner);

        assertTrue(projectRepository.findAll().isEmpty());

        assertNull(project.getId());
        assertEquals(project.getTitle(), title);
        assertEquals(project.getDescription(), description);
        assertEquals(project.getOwner(), owner);

        projectRepository.save(project);

        assertFalse(projectRepository.findAll().isEmpty());

        List<Project> projects = projectRepository.findAll();

        assertEquals(1, projects.size());

        val savedProject = projects.get(0);

        assertNotNull(savedProject.getId());
        assertEquals(savedProject.getTitle(), title);
        assertEquals(savedProject.getDescription(), description);
        assertEquals(savedProject.getOwner(), owner);
    }

    @Test
    void Test002_ProjectRepositoryWhenGivenTitleShouldReturnProjectWithThatTitle() {
        userRepository.save(owner);

        projectRepository.save(project);

        assertTrue(projectRepository.findByTitle(title).isPresent());
    }

    @Test
    void Test003_ProjectRepositoryWhenGivenNonExistingTitleShouldReturnEmptyList() {
        userRepository.save(owner);

        projectRepository.save(project);

        assertTrue(projectRepository.findByTitle("Another title").isEmpty());
    }

    @Test
    void Test004_ProjectRepositoryWhenDeletedOnlyExistingProjectShouldReturnEmptyList() {
        userRepository.save(owner);

        projectRepository.save(project);
        List<Project> projects = projectRepository.findAll();
        val savedProject = projects.get(0);
        projectRepository.deleteById(savedProject.getId());
        List<Project> projects1 = projectRepository.findAll();
        assertEquals(0, projects1.size());
    }

    @Test
    void Test005_GivenSomeSavedProjectsWithTheSameOwnerWhenDeletingByOwnerThenTheyAreDeleted() {
        userRepository.save(owner);

        projectRepository.save(project);
        projectRepository.save(
                Project.builder()
                        .title("Project Title")
                        .description("description")
                        .owner(owner)
                        .build());
        assertEquals(2, projectRepository.findAll().size());

        projectRepository.deleteByOwner(owner);
        assertTrue(projectRepository.findAll().isEmpty());
    }

    @Test
    void
            Test006_GivenTwoSavedProjectsWithDifferentOwnersWhenDeletingByOwnerThenTheOtherProjectRemains() {
        userRepository.save(owner);
        User owner2 =
                User.builder()
                        .nickname("JohnDoe")
                        .email("john@mail.com")
                        .password("12345678")
                        .build();
        userRepository.save(owner2);

        projectRepository.save(project);
        projectRepository.save(
                Project.builder()
                        .title("Project Title")
                        .description("description")
                        .owner(owner2)
                        .build());
        assertEquals(2, projectRepository.findAll().size());

        projectRepository.deleteByOwner(owner);
        assertEquals(1, projectRepository.findAll().size());

        assertEquals("JohnDoe", projectRepository.findAll().get(0).getOwner().getNickname());
    }

    @Test
    void Test007_ProjectRepositoryWhenGivenTagNameShouldReturnAllProjectsThatHaveThatTag() {
        userRepository.save(owner);

        Tag tag1 = Tag.builder().name("tag1").build();
        Tag tag2 = Tag.builder().name("tag2").build();
        Tag tag3 = Tag.builder().name("tag3").build();

        Project project1 =
                Project.builder()
                        .title("Project1")
                        .description("My description")
                        .owner(owner)
                        .links(Arrays.asList("link1", "link2"))
                        .tags(Arrays.asList(tag1, tag2))
                        .build();

        Project project2 =
                Project.builder()
                        .title("Project2")
                        .description("My description")
                        .owner(owner)
                        .links(Arrays.asList("link1", "link2"))
                        .tags(Arrays.asList(tag1, tag3))
                        .build();

        Project project3 =
                Project.builder()
                        .title("Project3")
                        .description("My description")
                        .owner(owner)
                        .links(Arrays.asList("link1", "link2"))
                        .tags(Arrays.asList(tag1, tag3))
                        .build();

        projectRepository.save(project1);
        projectRepository.save(project2);
        projectRepository.save(project3);

        List<Project> projectsWithTag3 = projectRepository.findProjectsByTagName("tag3");

        assertEquals(2, projectsWithTag3.size());
        assertTrue(projectsWithTag3.contains(project2));
        assertTrue(projectsWithTag3.contains(project3));
    }

    @Test
    public void givenLast_whenGettingListOfUsers_thenCorrect() {
        userRepository.save(owner);

        assertTrue(projectRepository.findAll().isEmpty());

        assertNull(project.getId());
        assertEquals(project.getTitle(), title);
        assertEquals(project.getDescription(), description);
        assertEquals(project.getOwner(), owner);

        projectRepository.save(project);

        ProjectSpecification spec = new ProjectSpecification(new SearchCriteria("title", "New"));

        List<Project> results = projectRepository.findAll(spec);

        assertEquals(project, results.get(0));
    }
}
