package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.exception.InvalidProjectCollaborationApplicationException;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.model.ProjectDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.ProjectUserDTO;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.UserService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceActiveTest extends AbstractServiceTest {

    @Autowired private ProjectService projectService;

    @Autowired private UserService userService;

    @Autowired private UserRepository userRepository;

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test001_ProjectServiceWhenReceivesValidProjectApplicationShouldUpdateApplicantsList() {

        User loggedUser = userService.getLoggedUser();

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("GNU/Linux").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertFalse(
                project.getApplicants().stream()
                        .map(ProjectUserDTO::getNickname)
                        .collect(Collectors.toList())
                        .contains(loggedUser.getNickname()));

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
            Test003_ProjectServiceWhenReceivesApplicationToAnAlreadyCollaboratingProjectToShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("Node.js").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test004_ProjectServiceWhenReceivesApplicationToAnAlreadyAppliedProjectToShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("TensorFlow").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test005_ProjectServiceWhenWhenOwnerAppliesToCollaborateShouldThrowException() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("RedHatAnsible").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidProjectCollaborationApplicationException.class,
                () -> projectService.applyToProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test006_ProjectServiceWhenFindingProjectsByCollaboratorsContainingUserThenProjectsAreReturned() {
        List<ProjectDTO> collaboratingProjects =
                projectService.getCollaboratingProjects(userService.getLoggedUser());

        assertEquals(2, collaboratingProjects.size());

        List<String> collaboratingProjectTitles =
                collaboratingProjects.stream()
                        .map(ProjectDTO::getTitle)
                        .collect(Collectors.toList());
        assertTrue(collaboratingProjectTitles.contains("Node.js"));
        assertTrue(collaboratingProjectTitles.contains("Django"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test007_ProjectServiceWhenFindingProjectsByOwnerThenProjectsAreReturned() {
        List<ProjectDTO> ownedProjects =
                projectService.getProjectsByOwner(userService.getLoggedUser());

        assertEquals(4, ownedProjects.size());
        List<String> ownedProjectTitles =
                ownedProjects.stream().map(ProjectDTO::getTitle).collect(Collectors.toList());
        assertTrue(ownedProjectTitles.contains("TensorFlow"));
        assertTrue(ownedProjectTitles.contains("Renovate"));
        assertTrue(ownedProjectTitles.contains("Kubernetes"));
        assertTrue(ownedProjectTitles.contains("RedHatAnsible"));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test008_ProjectServiceWhenNotProjectOwnerRequestsForApplicantsShouldThrowException() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("TensorFlow").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertThrows(
                InvalidUserException.class,
                () -> projectService.getProjectApplicants(project.getId()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test009_ProjectServiceWhenProjectOwnerWithValidProjectIdRequestsForApplicantsShouldReturnProjectUserDTOs() {

        ProjectSearchDTO projectSearchDTO = ProjectSearchDTO.builder().title("TensorFlow").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        val applicants = projectService.getProjectApplicants(project.getId());
        assertNotNull(applicants);
        assertEquals(1, applicants.size());
        assertEquals("Peltevis", applicants.get(0).getNickname());
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test010_ProjectServiceWhenGettingApplicantsToProjectWithInvalidIdShouldThrowException() {

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.getProjectApplicants(UUID.randomUUID()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test011_ProjectServiceWhenApplyingToProjectWithInvalidIdShouldThrowException() {

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.applyToProject(UUID.randomUUID()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test011_ProjectServiceWhenAcceptingApplicationWithInvalidProjectIdShouldThrowException() {

        var applicant = userRepository.findByNickname("FabriDS23");
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.acceptApplicant(UUID.randomUUID(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test012_ProjectServiceWhenRejectingApplicationWithInvalidProjectIdShouldThrowException() {

        var applicant = userRepository.findByNickname("FabriDS23");
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.rejectApplicant(UUID.randomUUID(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test013_ProjectServiceWhenNotProjectOwnerAcceptsApplicantsShouldThrowException() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("ropa1998");

        assertThrows(
                InvalidUserException.class,
                () -> projectService.acceptApplicant(project.getId(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test014_ProjectServiceWhenNotProjectOwnerRejectsApplicantsShouldThrowException() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("ropa1998");

        assertThrows(
                InvalidUserException.class,
                () -> projectService.rejectApplicant(project.getId(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test015_ProjectServiceWhenAcceptingInvalidApplicantsShouldThrowException() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("Peltevis");

        assertThrows(
                InvalidUserException.class,
                () -> projectService.acceptApplicant(project.getId(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test016_ProjectServiceWhenRejectingInvalidApplicantsShouldThrowException() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("Peltevis");

        assertThrows(
                InvalidUserException.class,
                () -> projectService.rejectApplicant(project.getId(), applicant.get().getId()));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void
            Test017_ProjectServiceWhenAcceptingValidApplicantsShouldUpdateApplicantAndCollaboratorsList() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("ropa1998").get();

        assertTrue(
                project.getApplicants().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
        assertFalse(
                project.getCollaborators().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));

        projectService.acceptApplicant(project.getId(), applicant.getId());

        val updatedProject = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertTrue(
                updatedProject.getCollaborators().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
        assertFalse(
                updatedProject.getApplicants().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test018_ProjectServiceWhenRejectingValidApplicantsShouldUpdateApplicantList() {

        ProjectSearchDTO projectSearchDTO =
                ProjectSearchDTO.builder().title("ApacheCassandra").build();
        val project = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        var applicant = userRepository.findByNickname("ropa1998").get();

        assertTrue(
                project.getApplicants().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
        assertFalse(
                project.getCollaborators().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));

        projectService.rejectApplicant(project.getId(), applicant.getId());
        val updatedProject = projectService.searchProjectsByFilter(projectSearchDTO).get(0);

        assertFalse(
                updatedProject.getCollaborators().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
        assertFalse(
                updatedProject.getApplicants().stream()
                        .map(ProjectUserDTO::getId)
                        .collect(Collectors.toList())
                        .contains(applicant.getId()));
    }
}
