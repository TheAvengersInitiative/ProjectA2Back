package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a2.backend.BackendApplication;
import com.a2.backend.entity.*;
import java.util.Collections;
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
public class DiscussionRepositoryTest {
    @Autowired private ProjectRepository projectRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private DiscussionRepository discussionRepository;
    String title = "New project";
    String description = "Testing project repository";
    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();
    List<Tag> tags = Collections.singletonList(Tag.builder().name("Start").build());
    List<String> links = Collections.singletonList("http://link.com");
    Project project =
            Project.builder()
                    .title(title)
                    .description(description)
                    .tags(tags)
                    .links(links)
                    .owner(owner)
                    .applicants(List.of())
                    .collaborators(List.of())
                    .rejectedApplicants(List.of())
                    .build();

    Discussion discussion =
            Discussion.builder().title("Discussion").project(project).tags(tags).build();

    @Test
    void Test001_DiscussionRepositoryShouldSaveDiscussions() {
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

        discussionRepository.save(discussion);
        assertFalse(discussionRepository.findAll().isEmpty());

        List<Discussion> discussions = discussionRepository.findAll();
        assertEquals(1, discussions.size());
    }

    @Test
    void Test002_DiscussionRepositoryShouldgetDiscussionByProjectAndTitle() {
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

        discussionRepository.save(discussion);
        Discussion savedDiscussion =
                discussionRepository.findByProjectIdAndTitle(
                        savedProject.getId(), discussion.getTitle());
        assertEquals(savedDiscussion.getTags().get(0).getId(), tags.get(0).getId());
        assertEquals(savedDiscussion.getProject().getId(), savedProject.getId());
    }

    @Test
    void Test003_DiscussionRepositoryShouldNotGetDiscussionByProjectAndTitle() {
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

        discussionRepository.save(discussion);
        Discussion savedDiscussion =
                discussionRepository.findByProjectIdAndTitle(savedProject.getId(), "NotDiscussion");
    }
}
