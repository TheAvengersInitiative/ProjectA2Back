package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.exception.InvalidProjectCollaborationApplicationException;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.ProjectUserDTO;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

public class ProjectServiceActiveTest extends AbstractServiceTest {

    @Autowired private ProjectService projectService;

    @Autowired private UserService userService;

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test001_ProjectServiceWhenReceivesValidProjectApplicationShouldUpdateApplicantsList() {

        User loggedUser = userService.getLoggedUser();

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("GNU/Linux").build();
        Project project = projectService.searchProjecsByFilter(projectSearchDTO).get(0);

        assertFalse(project.getApplicants().contains(loggedUser));

        val projectDTO = projectService.applyToProject(project.getId());

        assertTrue(
                projectDTO.getApplicants().stream()
                        .map(ProjectUserDTO::getEmail)
                        .collect(Collectors.toList())
                        .contains(loggedUser.getEmail()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test002_ProjectServiceWhenReceivesApplicationToAnAlreadyRejectedCollaborationProjectToShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("GNU/Linux").build();
        Project project = projectService.searchProjecsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test003_ProjectServiceWhenReceivesApplicationToAnAlreadyCollaboratingProjectToShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("Node.js").build();
        Project project = projectService.searchProjecsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test004_ProjectServiceWhenReceivesApplicationToAnAlreadyAppliedProjectToShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("TensorFlow").build();
        Project project = projectService.searchProjecsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }
}
