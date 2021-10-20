package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class ProjectControllerActiveTest extends AbstractControllerTest {

    @Autowired MockMvc mvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired DiscussionRepository discussionRepository;

    @Autowired ProjectRepository projectRepository;

    @Autowired UserRepository userRepository;

    private final String baseUrl = "/project";

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test001_ProjectControllerFindAllProjects() throws Exception {
        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertNotNull(projects);
        assertEquals(11, projects.length);
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test002_ProjectControllerFindMyProjects() throws Exception {
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(baseUrl + "/my-projects")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertNotNull(projects);
        assertEquals(5, projects.length);
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test003_ProjectControllerWithApplicationToAnAlreadyAppliedProjectToShouldReturnStatusBadRequest()
                    throws Exception {
        val project = projectRepository.findByTitle("TensorFlow");

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/apply",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Already applied to project"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test004_ProjectControllerWithApplicationToAnAlreadyCollaboratingProjectToShouldReturnStatusBadRequest()
                    throws Exception {
        val project = projectRepository.findByTitle("Node.js");

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/apply",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Already collaborating in project"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test005_ProjectControllerWithValidApplicationToProjectToShouldReturnHttpOkTest()
            throws Exception {
        val project = projectRepository.findByTitle("GNU/Linux");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/apply",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ProjectDTO projectDTO = objectMapper.readValue(contentAsString, ProjectDTO.class);

        assert projectDTO != null;
        assertEquals(project.get().getTitle(), projectDTO.getTitle());
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test006_ProjectControllerWhenOwnerAppliesToCollaborateShouldReturnStatusBadRequest()
            throws Exception {
        val project = projectRepository.findByTitle("Node.js");

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/apply",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Current user owns project"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test007_ProjectControllerWhenProjectOwnerWithValidProjectIdRequestsForApplicantsShouldReturnHttpOkTest()
                    throws Exception {
        val project = projectRepository.findByTitle("TensorFlow");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/applicants",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ProjectUserDTO[] users = objectMapper.readValue(contentAsString, ProjectUserDTO[].class);

        assertNotNull(users);
        assertEquals(1, users.length);
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test008_ProjectControllerWhenNotProjectOwnerRequestsForApplicantsShouldReturnBadRequest()
            throws Exception {
        val project = projectRepository.findByTitle("TensorFlow");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/applicants",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test009_ProjectControllerWhenAcceptingValidApplicantShouldReturnHttpOkTest()
            throws Exception {
        val project = projectRepository.findByTitle("ApacheCassandra");
        val applicant = userRepository.findByNickname("ropa1998");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/accept",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                applicant.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ProjectUserDTO[] updatedApplicants =
                objectMapper.readValue(contentAsString, ProjectUserDTO[].class);

        assertNotNull(updatedApplicants);
        assertEquals(0, updatedApplicants.length);
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test010_ProjectControllerWhenAcceptingNotValidApplicantShouldReturnBadRequest()
            throws Exception {
        val project = projectRepository.findByTitle("ApacheCassandra");
        val applicant = userRepository.findByNickname("Peltevis");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/accept",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                applicant.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test011_ProjectControllerWhenRejectingValidApplicantShouldReturnHttpOkTest()
            throws Exception {
        val project = projectRepository.findByTitle("ApacheCassandra");
        val applicant = userRepository.findByNickname("ropa1998");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/reject",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                applicant.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ProjectUserDTO[] updatedApplicants =
                objectMapper.readValue(contentAsString, ProjectUserDTO[].class);

        assertNotNull(updatedApplicants);
        assertEquals(0, updatedApplicants.length);
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test012_ProjectControllerWhenRejectingNotValidApplicantShouldReturnBadRequest()
            throws Exception {
        val project = projectRepository.findByTitle("ApacheCassandra");
        val applicant = userRepository.findByNickname("Peltevis");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/reject",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                applicant.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test0013_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNotAuthorizedUserShouldReturnUnauthorized()
                    throws Exception {

        val project = projectRepository.findByTitle("GNU/Linux");
        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.get().getId().toString()
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test014_ProjectControllerWhenReceivesInvalidProjectIdShouldReturnBadRequest()
            throws Exception {

        val collaborator = userRepository.findByNickname("Peltevis");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(collaborator.get().getId())
                        .score(5)
                        .comment("Did a great job")
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/review",
                                                        Objects.requireNonNull(UUID.randomUUID())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("The project with that id"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test015_ProjectControllerWhenLoggedUserIsNotProjectOwnerWhenSubmittingReviewShouldReturnBadRequest()
                    throws Exception {

        val project = projectRepository.findByTitle("Geany");

        val collaborator = userRepository.findByNickname("Peltevis");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(collaborator.get().getId())
                        .score(5)
                        .comment("Did a great job")
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/review",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only project owners can submit reviews"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void
            Test016_ProjectControllerWhenTryingToReviewSomeoneWhoDoesNotCollaborateInProjectShouldReturnBadRequest()
                    throws Exception {

        val project = projectRepository.findByTitle("Geany");

        val collaborator = userRepository.findByNickname("ropa1998");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(collaborator.get().getId())
                        .score(5)
                        .comment("Did a great job")
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/review",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("does not collaborate in project"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test017_ProjectControllerWhenTryingToReviewUserNotFoundShouldReturnBadRequest()
            throws Exception {

        val project = projectRepository.findByTitle("Geany");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(UUID.randomUUID())
                        .score(5)
                        .comment("Did a great job")
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/review",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("does not collaborate in project"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void
            Test018_ProjectControllerWhenReceivingValidReviewCreateDTOAndValidProjectIDShouldReturnHttpOkTest()
                    throws Exception {

        val project = projectRepository.findByTitle("Geany");

        val collaborator = userRepository.findByNickname("Peltevis");

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder()
                        .collaboratorID(collaborator.get().getId())
                        .score(5)
                        .comment("Did a great job")
                        .build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl + "/review",
                                                        Objects.requireNonNull(
                                                                project.get().getId())))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void
            Test019_ProjectControllerWithNotValidProjectIdWhenAskingForCollaboratorsReviewsShouldReturnBadRequest()
                    throws Exception {

        val collaborator = userRepository.findByNickname("ropa1998");

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/reviews",
                                                        Objects.requireNonNull(UUID.randomUUID()),
                                                        Objects.requireNonNull(
                                                                collaborator.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("The project with that id"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test020_ProjectControllerWhenNotProjectOwnerAsksForCollaboratorsReviewsShouldReturnBadRequest()
                    throws Exception {

        val project = projectRepository.findByTitle("Node.js");

        val collaborator = userRepository.findByNickname("ropa1998");

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/reviews",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                collaborator.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only project owners can see collaborator reviews"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void
            Test021_ProjectControllerWithValidProjectIdAndUserIDWhenAskingForCollaboratorsReviewsShouldReturnHttpOkTest()
                    throws Exception {

        val project = projectRepository.findByTitle("Node.js");

        val collaborator = userRepository.findByNickname("ropa1998");

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format(
                                                        "%s/%s/%s",
                                                        baseUrl + "/reviews",
                                                        Objects.requireNonNull(
                                                                project.get().getId()),
                                                        Objects.requireNonNull(
                                                                collaborator.get().getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ReviewDTO[] reviews = objectMapper.readValue(contentAsString, ReviewDTO[].class);
        assertNotNull(reviews);
        assertEquals(2, reviews.length);
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test022_ProjectControllerWithValidReviewCreateDTOWhenCreatingReviewThenUserReputationIsUpdated()
                    throws Exception {
        val project = projectRepository.findByTitle("Django").get();

        User user = userRepository.findByNickname("ropa1998").get();
        assertEquals(2.5, user.getReputation());

        ReviewCreateDTO reviewCreateDTO =
                ReviewCreateDTO.builder().collaboratorID(user.getId()).score(5).build();

        mvc.perform(
                        MockMvcRequestBuilders.put(baseUrl + "/review/" + project.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewCreateDTO))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(4, user.getReputation());
    }

    // Search projects by title only with value "KaI" should return only Sakai project
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0041_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch = ProjectSearchDTO.builder().title("KaI").build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("Sakai"));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects by title only with value "KuBER" and featured should return only Kubernetes
     * project *
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0042_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().title("KuBER").featured(true).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("Kubernetes"));
        assertEquals(1, projects.length);
    }
    /**
     * Search projects only by languages with value Java should return projects sakai, apache, and
     * renovate project *
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0043_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("Java")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        for (Project project : projects) {
            assertTrue(
                    project.getTitle().equals("Sakai")
                            || project.getTitle().equals("ApacheCassandra")
                            || project.getTitle().equals("Renovate"));
        }
        assertEquals(3, projects.length);
    }

    /**
     * Search projects only by languages with value java should return projects sakai, apache and
     * renovate
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0044_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("java")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(3, projects.length);
        for (Project project : projects) {
            assertTrue(
                    project.getTitle().equals("Sakai")
                            || project.getTitle().equals("ApacheCassandra")
                            || project.getTitle().equals("Renovate"));
        }
    }

    /** Search projects only by languages with value Jav should return empty project list */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0045_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("Jav")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertEquals(0, projects.length);
    }

    /** Search projects only by languages with value Java and should return empty project list */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0046_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("Jav")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertEquals(0, projects.length);
    }

    /**
     * Search projects only by languages with value java and javascript should return project
     * renovate
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0047_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("java", "javascript")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("Renovate"));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects only by tags with value tOol should return projects ansible and flask
     * renovate
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0048_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().tags(Arrays.asList("tOol")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(2, projects.length);
        for (Project project : projects) {
            assertTrue(
                    project.getTitle().equals("RedHatAnsible")
                            || project.getTitle().equals("Flask"));
        }
    }

    /** Search projects only by tags with value tOo should return empty project list */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0049_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().tags(Arrays.asList("tOo")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertEquals(0, projects.length);
    }

    /** Search projects only by tags with value tOol and automation should return project ansible */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0050_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().tags(Arrays.asList("tOol", "automation")).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(
                projects[0]
                        .getTitle()
                        .equals(projectRepository.findByTitle("RedHatAnsible").get().getTitle()));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects only by tags and languages with values java and bigdata should return project
     * apachecassandra
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0051_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("big data"))
                        .languages(Arrays.asList("java"))
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("ApacheCassandra"));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects only by tags and languages with values java and bigdata should return project
     * apachecassandra
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0052_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("big data"))
                        .languages(Arrays.asList("java"))
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("ApacheCassandra"));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects by tags, languages and title with values java and dependency and title "A"
     * should return projects apache cassandra and renovate
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0053_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("dependency"))
                        .languages(Arrays.asList("java"))
                        .title("A")
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        for (Project project : projects) {
            assertTrue(
                    project.getTitle().equals("ApacheCassandra")
                            || project.getTitle().equals("Renovate"));
        }
        assertEquals(2, projects.length);
    }

    /**
     * Search projects by tags, languages and title with values java and dependency and title
     * "flask" should return nothing
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0054_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("dependency"))
                        .languages(Arrays.asList("java"))
                        .title("flask")
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(0, projects.length);
    }

    /**
     * Search projects by tags, languages and title with values java and dependency and title
     * "apache" should return project apache
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0055_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("dependency"))
                        .languages(Arrays.asList("java"))
                        .title("apache")
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertTrue(projects[0].getTitle().equals("ApacheCassandra"));
        assertEquals(1, projects.length);
    }

    /**
     * Search projects by tags, languages and featured with values python and tool and true should
     * return project Ansible
     */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0056_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .tags(Arrays.asList("tool"))
                        .languages(Arrays.asList("python"))
                        .featured(true)
                        .build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(projects[0].getTitle(), "RedHatAnsible");
        assertEquals(1, projects.length);
    }

    /** Search all project with page value 1 should return 3 projects */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0057_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch = ProjectSearchDTO.builder().page(1).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertEquals(3, projects.length);
    }

    /** Search all project with page value 0 should return 8 projects */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0058_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch = ProjectSearchDTO.builder().page(0).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertEquals(8, projects.length);
    }

    /** Search all featured project with page value 1 should return 0 projects */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0059_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().featured(true).page(1).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(0, projects.length);
    }

    /** Test pagination when searching for featured projects */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0060_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().featured(true).page(0).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(4, projects.length);
    }

    /** search for featured projects should return four projects */
    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test0061_ProjectSearch() throws Exception {
        ProjectSearchDTO projectToSearch = ProjectSearchDTO.builder().featured(true).build();
        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(4, projects.length);
    }
}
