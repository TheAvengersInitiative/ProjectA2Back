package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.entity.Project;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.model.ProjectDTO;
import com.a2.backend.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    @Autowired ProjectRepository projectRepository;

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
        assertEquals(10, projects.length);
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
        assertEquals(4, projects.length);
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
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test005_ProjectControllerWithApplicationToAnAlreadyRejectedCollaborationProjectToShouldReturnStatusBadRequest()
                    throws Exception {
        val project = projectRepository.findByTitle("GNU/Linux");

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

        assert (Objects.requireNonNull(errorMessage).contains("Already rejected collaboration"));
    }

    @Test
    @WithMockUser(username = "fabrizio.disanto@ing.austral.edu.ar")
    void Test006_ProjectControllerWithValidApplicationToProjectToShouldReturnHttpOkTest()
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
    void Test007_ProjectControllerWhenOwnerAppliesToCollaborateShouldReturnStatusBadRequest()
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
            Test0032_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNotAuthorizedUserShouldReturnUnauthorized()
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
}
