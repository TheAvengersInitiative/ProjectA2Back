package com.a2.backend.controller;

import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.model.*;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class DiscussionControllerActiveTest extends AbstractControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired DiscussionRepository discussionRepository;

    private final String baseUrl = "/discussion";

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test001_DiscussionControllerWithValidCommentCreateDTOAndDiscussionIdWhenCreatingCommentThenStatusIsOk()
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
    void Test002_DiscussionControllerWithBlankCommentWhenCreatingCommentThenBadRequestIsReturned()
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
    void Test003_DiscussionControllerWithNullCommentWhenCreatingCommentThenBadRequestIsReturned()
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
    void Test004_DiscussionControllerWithValidCommentIdWhenHighlightingShouldReturnHttpOkTest()
            throws Exception {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHighlighted());

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/highlight/" + comment.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val updatedComment = objectMapper.readValue(contentAsString, CommentDTO.class);

        assertEquals(comment.getId(), updatedComment.getId());
        assertTrue(updatedComment.isHighlighted());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test005_DiscussionControllerWithNotValidCommentIdWhenHighlightingShouldReturnBadRequest()
            throws Exception {

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/highlight/" + UUID.randomUUID())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Discussion with comment id"));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test006_DiscussionControllerWithValidCommentIdButNotOwnerWhenHighlightingShouldReturnBadRequest()
                    throws Exception {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/highlight/" + comment.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only project owners can highlight comments"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test007_DiscussionControllerWithValidCommentIdWhenHidingShouldReturnHttpOkTest()
            throws Exception {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        assertFalse(comment.isHidden());

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl + "/hide/" + comment.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val updatedComment = objectMapper.readValue(contentAsString, CommentDTO.class);

        assertEquals(comment.getId(), updatedComment.getId());
        assertTrue(updatedComment.isHidden());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test008_DiscussionControllerWithNotValidCommentIdWhenHidingShouldReturnBadRequest()
            throws Exception {

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl + "/hide/" + UUID.randomUUID())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Discussion with comment id"));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test009_DiscussionControllerWithValidCommentIdButNotOwnerWhenHidingShouldReturnBadRequest()
            throws Exception {
        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(0);

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl + "/hide/" + comment.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only project owners can hide comments"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test010_DiscussionControllerWithNotValidDiscussionIdWhenGettingAllCommentsShouldReturnBadRequest()
                    throws Exception {

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                baseUrl + "/comments/" + UUID.randomUUID())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("The discussion with id"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test011_DiscussionControllerWithValidDiscussionIdWhenGettingCommentsAsOwnerShouldReturnHttpOkTest()
                    throws Exception {

        val discussion = projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                baseUrl + "/comments/" + discussion.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val comments = objectMapper.readValue(contentAsString, CommentDTO[].class);

        assertNotNull(comments);
        assertEquals(2, comments.length);
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test012_DiscussionControllerWithValidDiscussionIdWhenGettingCommentsAsCollaboratorShouldReturnHttpOkTest()
                    throws Exception {

        val discussion = projectRepository.findByTitle("Django").get().getDiscussions().get(0);

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                baseUrl + "/comments/" + discussion.getId())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val comments = objectMapper.readValue(contentAsString, CommentDTO[].class);

        assertNotNull(comments);
        assertEquals(3, comments.length);
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test0013_DiscussionControllerWhenReceivesValidUpdateDiscussionDTOshouldReturnOK()
            throws Exception {

        val project = projectRepository.findByTitle("Renovate");
        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                        .title(discussionTitle)
                        .build();

        DiscussionCreateDTO updatedDiscussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title("AnotherName")
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
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
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
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

        String changedDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.get().getId().toString()
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        updatedDiscussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals(
                projectRepository.findByTitle("Renovate").get().getDiscussions().get(0).getTitle(),
                "AnotherName");
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test0014_DiscussionControllerWhenReceivesValidUpdateDiscussionDTOButNotFromOwnerShouldReturnUnauthorized()
                    throws Exception {
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");
        DiscussionUpdateDTO discussionUpdateDTO =
                DiscussionUpdateDTO.builder()
                        .title("AnotherName")
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                        .forumTags(discussionTags)
                        .build();

        String updatedDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                "/discussion/"
                                                        + discussionRepository
                                                                .findByTitle(
                                                                        "Issue with User ViewSet")
                                                                .get()
                                                                .getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isUnauthorized())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test0015_DiscussionControllerWhenReceivesValidUpdateDiscussionDTOFromOwnerShouldReturnStatusOk()
                    throws Exception {
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");
        DiscussionUpdateDTO discussionUpdateDTO =
                DiscussionUpdateDTO.builder()
                        .title("AnotherName")
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                        .forumTags(discussionTags)
                        .build();
        String updatedDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                "/discussion/"
                                                        + discussionRepository
                                                                .findByTitle(
                                                                        "Issue with User ViewSet")
                                                                .get()
                                                                .getId())
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

    @Test
    @WithMockUser(username = "franz.sotoleal@ing.austral.edu.ar")
    void Test016_DiscussionControllerWhenDeletingCommentShouldReturnStatusNoContent()
            throws Exception {
        Discussion discussion = discussionRepository.findByTitle("Not working on MacOS").get();
        UUID id = discussion.getComments().get(0).getId();

        mvc.perform(
                        MockMvcRequestBuilders.delete(baseUrl + "/comment/" + id)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "franz.sotoleal@ing.austral.edu.ar")
    void Test017_DiscussionControllerWithInvalidCommentIdWhenDeletingCommentShouldReturnBadRequest()
            throws Exception {
        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.delete(
                                                baseUrl + "/comment/" + UUID.randomUUID())
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Discussion with comment id"));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test018_DiscussionControllerWithInvalidUserWhenDeletingCommentShouldReturnBadRequest()
            throws Exception {
        Discussion discussion = discussionRepository.findByTitle("Not working on MacOS").get();
        UUID id = discussion.getComments().get(0).getId();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.delete(baseUrl + "/comment/" + id)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only comment owners can delete comments"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test019_DiscussionControllerWithValidCommentIdAndCommentUpdateDTOWhenUpdatingShouldReturnHttpOkTest()
                    throws Exception {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        CommentUpdateDTO commentUpdateDTO =
                CommentUpdateDTO.builder().comment("updated comment").build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment-update/" + comment.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val updatedComment = objectMapper.readValue(contentAsString, CommentDTO.class);

        assertNotNull(updatedComment);
        assertEquals("updated comment", updatedComment.getComment());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test020_DiscussionControllerWithValidCommentIdAndBlankCommentUpdateDTOWhenUpdatingShouldReturnBadRequest()
                    throws Exception {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        CommentUpdateDTO commentUpdateDTO =
                CommentUpdateDTO.builder().comment("  \n  \r  \t  ").build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment-update/" + comment.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Comment cannot be empty"));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test021_DiscussionControllerWithNotValidCommentIdWhenUpdatingShouldReturnBadRequest()
            throws Exception {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        CommentUpdateDTO commentUpdateDTO =
                CommentUpdateDTO.builder().comment("update comment").build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment-update/" + UUID.randomUUID())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("Discussion with comment"));
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test022_DiscussionControllerWithValidCommentIdAndCommentUpdateDTOButLoggedUserIsNotCreatorWhenUpdatingShouldReturnBadRequest()
                    throws Exception {

        val comment =
                projectRepository
                        .findByTitle("Kubernetes")
                        .get()
                        .getDiscussions()
                        .get(0)
                        .getComments()
                        .get(1);

        CommentUpdateDTO commentUpdateDTO =
                CommentUpdateDTO.builder().comment("update comment").build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                baseUrl + "/comment-update/" + comment.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage)
                .contains("Only comment creator can update comment"));
    }

    @Test
    @WithMockUser(username = "franz.sotoleal@ing.austral.edu.ar")
    void Test023_DiscussionControllerDeleteDiscussion() throws Exception {
        val discussion = projectRepository.findByTitle("Kubernetes").get().getDiscussions().get(0);

        mvc.perform(
                        MockMvcRequestBuilders.delete(baseUrl + "/" + discussion.getId())
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(1, projectRepository.findByTitle("Kubernetes").get().getDiscussions().size());
    }
}
