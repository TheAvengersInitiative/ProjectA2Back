package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.Project;
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

    String title = "New project";
    String description = "Testing project repository";
    String owner = "Owner´s name";

    Project project = Project.builder().title(title).description(description).owner(owner).build();

    @Test
    void Test001_ProjectRepositoryShouldSaveProjects() {

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

        projectRepository.save(project);

        assertTrue(projectRepository.findByTitle(title).isPresent());
    }

    @Test
    void Test003_ProjectRepositoryWhenGivenNonExistingTitleShouldReturnEmptyList() {

        projectRepository.save(project);

        assertTrue(projectRepository.findByTitle("Another title").isEmpty());
    }

    @Test
    void Test004_ProjectRepositoryWhenDeletedOnlyExistingProjectShouldReturnEmptyList() {
        projectRepository.save(project);
        List<Project> projects = projectRepository.findAll();
        val savedProject = projects.get(0);
        projectRepository.deleteById(savedProject.getId());
        List<Project> projects1 = projectRepository.findAll();
        assertEquals(0, projects1.size());
    }
}
