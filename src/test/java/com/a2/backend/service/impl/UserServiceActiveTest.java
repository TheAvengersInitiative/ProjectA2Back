package com.a2.backend.service.impl;

import com.a2.backend.constants.PrivacyConstant;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.exception.UserNotFoundException;
import com.a2.backend.model.ProjectDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.UserPrivacyDTO;
import com.a2.backend.model.UserProfileDTO;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceActiveTest extends AbstractServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
    Test001_GivenValidUserAndValidProjectsWhenWantToGetThePreferredProjectsThenReturnProjectList() {
        List<String> preferredTags = new ArrayList<>();
        preferredTags.add("Python");
        preferredTags.add("C");

        userService.getLoggedUser().setPreferredTags(preferredTags);
        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().tags(preferredTags).build();
        List<Project> preferredProjects = projectService.searchProjecsByFilter(projectSearchDTO);
        List<ProjectDTO> projects = userService.getPreferredProjects();
        assertEquals(6, projects.size());

        List<UUID> preferredProjectsId = new ArrayList<>();
        for (int i = 0; i < preferredProjects.size(); i++) {
            preferredProjectsId.add(preferredProjects.get(i).getId());
        }
        assertTrue(projects.get(0).isFeatured());
        assertTrue(projects.get(1).isFeatured());
        assertTrue(preferredProjectsId.contains(projects.get(2).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(3).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(4).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(5).getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
    Test002_GivenValidUserWithNotPreferredTagsWhenWantToGetProjectsThenReturnProjectListWithFourRandomProjectsAndTwoFeatured() {

        List<ProjectDTO> projects = userService.getPreferredProjects();

        assertTrue(projects.get(0).isFeatured());
        assertTrue(projects.get(1).isFeatured());
        assertEquals(6, projects.size());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test003_GivenNoFeaturedProjectsWhenWantPreferredProjectsThenFillWithNonFeaturedProjects() {
        List<String> preferredTags = new ArrayList<>();
        preferredTags.add("Python");
        preferredTags.add("C");
        userService.getLoggedUser().setPreferredTags(preferredTags);
        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().tags(preferredTags).build();
        List<Project> preferredProjects = projectService.searchProjecsByFilter(projectSearchDTO);
        List<ProjectDTO> projects = userService.getPreferredProjects();
        for (int i = 0; i < projects.size(); i++) {
            projects.get(i).setFeatured(false);
        }
        assertFalse(projects.get(0).isFeatured());
        assertFalse(projects.get(1).isFeatured());

        List<UUID> preferredProjectsId = new ArrayList<>();
        for (int i = 0; i < preferredProjects.size(); i++) {
            preferredProjectsId.add(preferredProjects.get(i).getId());
        }
        assertTrue(preferredProjectsId.contains(projects.get(2).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(3).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(4).getId()));
        assertTrue(preferredProjectsId.contains(projects.get(5).getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test004_GivenValidUserWhenUpdatingPreferencesThenTheyAreUpdated() {
        User loggedUser = userService.getLoggedUser();

        assertEquals(PrivacyConstant.PUBLIC, loggedUser.getTagsPrivacy());
        assertEquals(PrivacyConstant.PUBLIC, loggedUser.getLanguagesPrivacy());
        assertEquals(PrivacyConstant.PUBLIC, loggedUser.getCollaboratedProjectsPrivacy());
        assertEquals(PrivacyConstant.PUBLIC, loggedUser.getOwnedProjectsPrivacy());

        UserPrivacyDTO privacyDTO =
                UserPrivacyDTO.builder()
                        .tagsPrivacy(PrivacyConstant.PRIVATE)
                        .collaboratedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .languagesPrivacy(PrivacyConstant.PRIVATE)
                        .ownedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .build();

        User updatedUser = userService.updatePrivacySettings(privacyDTO);

        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getTagsPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getLanguagesPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getCollaboratedProjectsPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getOwnedProjectsPrivacy());
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test005_GivenValidUserWhenGettingProfileThenOnlyFieldsWithPublicPrivacyAreReturned() {
        User loggedUser = userService.getLoggedUser();
        UserProfileDTO userProfile = userService.getUserProfile(loggedUser.getId());

        assertEquals(loggedUser.getNickname(), userProfile.getNickname());
        assertEquals(loggedUser.getBiography(), userProfile.getBiography());
        assertEquals(loggedUser.getPreferredTags(), userProfile.getPreferredTags());
        assertNotNull(userProfile.getOwnedProjects());
        assertTrue(
                projectService.getProjectsByOwner(loggedUser).stream()
                        .map(Project::getId)
                        .collect(Collectors.toList())
                        .containsAll(
                                userProfile.getOwnedProjects().stream()
                                        .map(ProjectDTO::getId)
                                        .collect(Collectors.toList())));
        assertNull(userProfile.getCollaboratedProjects());
        assertNull(userProfile.getPreferredLanguages());
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test006_GivenANonExistentUserIdWhenGettingProfileThenExceptionIsThrown() {
        assertThrows(
                UserNotFoundException.class, () -> userService.getUserProfile(UUID.randomUUID()));
    }
}
