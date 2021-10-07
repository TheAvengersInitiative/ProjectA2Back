package com.a2.backend.controller;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ProjectControllerActiveTest extends AbstractControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProjectRepository projectRepository;

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

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
    Test023_ProjectControllerWithValidCommentCreateDTOAndDiscussionIdWhenCreatingCommentThenStatusIsOk()
            throws Exception {
        Discussion discussion =
                projectRepository.findByTitle("TensorFlow").get().getDiscussions().get(0);

        CommentCreateDTO commentCreateDTO =
                CommentCreateDTO.builder().comment("test comment").build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment/" + discussion.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Comment comment = objectMapper.readValue(contentAsString, Comment.class);

        assertEquals("test comment", comment.getComment());
        assertEquals("rodrigo.pazos@ing.austral.edu.ar", comment.getUser().getEmail());
        assertNotNull(comment.getDate());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test024_ProjectControllerWithBlankCommentWhenCreatingCommentThenBadRequestIsReturned()
            throws Exception {
        Discussion discussion =
                projectRepository.findByTitle("TensorFlow").get().getDiscussions().get(0);

        CommentCreateDTO commentCreateDTO =
                CommentCreateDTO.builder().comment("  \n  \r  \t  ").build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment/" + discussion.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Comment cannot be empty"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test025_ProjectControllerWithNullCommentWhenCreatingCommentThenBadRequestIsReturned()
            throws Exception {
        Discussion discussion =
                projectRepository.findByTitle("TensorFlow").get().getDiscussions().get(0);

        CommentCreateDTO commentCreateDTO = CommentCreateDTO.builder().comment(null).build();

        String errorMessage =
                mvc.perform(
                        MockMvcRequestBuilders.put(
                                baseUrl + "/comment/" + discussion.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentCreateDTO))
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Comment cannot be empty"));
    }
    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test0038_ProjectControllerWhenReceivesValidUpdateDiscussionDTOshouldReturnOK()
            throws Exception {

        val project = projectRepository.findByTitle("Renovate");
        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
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
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        DiscussionDTO createdDiscussion =
                objectMapper.readValue(discussionAsString, DiscussionDTO.class);
        DiscussionUpdateDTO discussionUpdateDTO =
                DiscussionUpdateDTO.builder()
                        .title("AnotherName")
                        .forumTags(discussionTags)
                        .build();

        String updatedDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                "/discussion/" + createdDiscussion.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }
}
