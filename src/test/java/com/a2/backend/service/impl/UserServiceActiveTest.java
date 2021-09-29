package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

public class UserServiceActiveTest extends AbstractServiceTest {
    @Autowired private UserService userService;

    @Autowired private ProjectService projectService;

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test001_GivenValidUserAndValidProjectsWhenWantToGetThePreferedProjectsThenReturnProjectList() {
        List<String> preferedTags = new ArrayList<>();
        preferedTags.add("Python");
        preferedTags.add("C");

        userService.getLoggedUser().setPreferredTags(preferedTags);
        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().tags(preferedTags).build();
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
            Test002_GivenValidUserWithNotPreferedTagsWhenWantToGetProjectsThenReturnProjectListWithfourRandomProjectsAndTwoFeatured() {

        List<ProjectDTO> projects = userService.getPreferredProjects();

        assertTrue(projects.get(0).isFeatured());
        assertTrue(projects.get(1).isFeatured());
        assertEquals(6, projects.size());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test003_GivenNoFeaturedProjectsWhenWantPreferedProjectsThenFillWithNonFeaturedProjects() {
        List<String> preferedTags = new ArrayList<>();
        preferedTags.add("Python");
        preferedTags.add("C");
        userService.getLoggedUser().setPreferredTags(preferedTags);
        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().tags(preferedTags).build();
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
}
