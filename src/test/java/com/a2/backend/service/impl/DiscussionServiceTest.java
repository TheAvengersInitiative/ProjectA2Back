package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.exception.DiscussionWithThatTitleExistsInProjectException;
import com.a2.backend.model.*;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DiscussionServiceImplTest extends AbstractTest {

    @Autowired private ProjectService projectService;

    @Autowired private TagService tagService;

    @Autowired private LanguageService languageService;

    @Autowired private ProjectRepository projectRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private DiscussionService discussionService;

    String title = "Project title";
    String discussiontitle = "Discussion title";
    String description = "Testing exception for existing title";
    List<String> links = Arrays.asList("link1", "link2");
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<String> forumTags = Arrays.asList("ftag1", "ftag2");
    List<String> discussionTags = Arrays.asList("desctag1", "desctag2");
    List<String> languages = Arrays.asList("Java", "C");

    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .preferredLanguages(List.of("Java", "C"))
                    .preferredTags(List.of("tag1", "tag3"))
                    .build();

    static List<String> linksUpdate = new ArrayList<>();
    static List<String> tagsUpdate = new ArrayList<>();
    List<String> languagesUpdate = Arrays.asList("Java", "Ruby");

    ProjectCreateDTO projectToCreate =
            ProjectCreateDTO.builder()
                    .title(title)
                    .description(description)
                    .links(links)
                    .tags(tags)
                    .forumTags(forumTags)
                    .languages(languages)
                    .build();
    ProjectCreateDTO secondProjectToCreate =
            ProjectCreateDTO.builder()
                    .title("Second Project")
                    .description(description)
                    .links(links)
                    .tags(tags)
                    .forumTags(forumTags)
                    .languages(languages)
                    .build();
    DiscussionCreateDTO discussionCreateDTO =
            DiscussionCreateDTO.builder().forumTags(discussionTags).title(discussiontitle).build();

    @BeforeAll
    static void setUp() {
        linksUpdate.add("http://google.com");
        tagsUpdate.add("tag1");
        tagsUpdate.add("tag4");
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test001_ServiceWhenReceivesValidCreateDiscussionDTOSaveDiscussion() {
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
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                project.getLanguages());
        assertEquals(projectToCreate.getLinks(), project.getLinks());

        // The whole owner is the same but when the project is persisted the persisted owner is
        // merged and thus .equals() returns false.
        // The same happens with preferred tags and languages collections.
        assertEquals(owner.getId(), project.getOwner().getId());
        assertEquals(owner.getNickname(), project.getOwner().getNickname());
        assertEquals(owner.getEmail(), project.getOwner().getEmail());
        assertEquals(owner.getPassword(), project.getOwner().getPassword());
        assertEquals(owner.getBiography(), project.getOwner().getBiography());
        assertTrue(
                owner.getPreferredLanguages()
                        .containsAll(project.getOwner().getPreferredLanguages()));
        assertTrue(owner.getPreferredTags().containsAll(project.getOwner().getPreferredTags()));
        val discussion = discussionService.createDiscussion(project.getId(), discussionCreateDTO);
        assertEquals(discussion.getTitle(), discussiontitle);
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test002_ServiceWhenReceivesExistingCreateDiscussionDTOShouldThrowException() {
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
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                project.getLanguages());
        assertEquals(projectToCreate.getLinks(), project.getLinks());

        // The whole owner is the same but when the project is persisted the persisted owner is
        // merged and thus .equals() returns false.
        // The same happens with preferred tags and languages collections.
        assertEquals(owner.getId(), project.getOwner().getId());
        assertEquals(owner.getNickname(), project.getOwner().getNickname());
        assertEquals(owner.getEmail(), project.getOwner().getEmail());
        assertEquals(owner.getPassword(), project.getOwner().getPassword());
        assertEquals(owner.getBiography(), project.getOwner().getBiography());
        assertTrue(
                owner.getPreferredLanguages()
                        .containsAll(project.getOwner().getPreferredLanguages()));
        assertTrue(owner.getPreferredTags().containsAll(project.getOwner().getPreferredTags()));
        val discussion = discussionService.createDiscussion(project.getId(), discussionCreateDTO);
        assertThrows(
                DiscussionWithThatTitleExistsInProjectException.class,
                () -> discussionService.createDiscussion(project.getId(), discussionCreateDTO));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test003_TheSameDiscussionCanExistinDifferentProjects() {
        userRepository.save(owner);
        assertTrue(projectService.getAllProjects().isEmpty());
        Project projectCreated = projectService.createProject(projectToCreate);
        Project secondProjectCreated = projectService.createProject(secondProjectToCreate);
        val projects = projectService.getAllProjects();
        assertFalse(projects.isEmpty());
        assertEquals(2, projects.size());
        val project = projects.get(0);
        val secondProject = projects.get(1);
        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
        assertEquals(tagService.findTagsByNames(projectToCreate.getTags()), project.getTags());
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                project.getLanguages());
        assertEquals(projectToCreate.getLinks(), project.getLinks());

        // The whole owner is the same but when the project is persisted the persisted owner is
        // merged and thus .equals() returns false.
        // The same happens with preferred tags and languages collections.
        assertEquals(owner.getId(), project.getOwner().getId());
        assertEquals(owner.getNickname(), project.getOwner().getNickname());
        assertEquals(owner.getEmail(), project.getOwner().getEmail());
        assertEquals(owner.getPassword(), project.getOwner().getPassword());
        assertEquals(owner.getBiography(), project.getOwner().getBiography());
        assertTrue(
                owner.getPreferredLanguages()
                        .containsAll(project.getOwner().getPreferredLanguages()));
        assertTrue(owner.getPreferredTags().containsAll(project.getOwner().getPreferredTags()));
        val discussion = discussionService.createDiscussion(project.getId(), discussionCreateDTO);
        assertEquals(discussion.getTitle(), discussiontitle);
        val secondDiscussion =
                discussionService.createDiscussion(secondProject.getId(), discussionCreateDTO);
        assertEquals(secondDiscussion.getTitle(), discussiontitle);
    }
}
