package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    @Autowired MockMvc mvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;

    private final String baseUrl = "/project";

    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test001_ProjectControllerWhenReceivesValidCreateProjectDTOShouldReturnStatusCreated()
            throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project project = objectMapper.readValue(contentAsString, Project.class);

        assertNotNull(project.getId());
        assertEquals(title, project.getTitle());
        assertEquals(description, project.getDescription());
        assertEquals(links, project.getLinks());
        assertEquals(owner, project.getOwner());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test002_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidTitleShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "a";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        assert (Objects.requireNonNull(errorMessage).contains("title"));
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test003_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Short";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("description"));
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test004_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionAndTitleShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "a";
        String description = "Short";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("description"));
        assert errorMessage.contains("title");
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test005_GivenNoExistingProjectsWhenGettingAllProjectsThenEmptyResponseIsReturned()
            throws Exception {

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assertNotNull(projects);
        assertEquals(0, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test006_GivenASingleExistingProjectWhenDeletedThenProjectIdIsReturned() throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assert projects != null;
        assertEquals(1, projects.length);

        String contentAsString2 =
                mvc.perform(
                                MockMvcRequestBuilders.delete(
                                                String.format(
                                                        "%s/%s", baseUrl, projects[0].getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()) //no content
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test007_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects()
            throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assert projects != null;
        assertEquals(1, projects.length);

        String contentAsString2 =
                mvc.perform(
                                MockMvcRequestBuilders.delete(
                                                String.format(
                                                        "%s/%s", baseUrl, projects[0].getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()) //no content
                        .andReturn()
                        .getResponse()
                        .getContentAsString();



        String contentAsString3 =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] emptyProjects = objectMapper.readValue(contentAsString3, Project[].class);

        assert emptyProjects != null;
        assertEquals(0, emptyProjects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test008_GivenASingleExistingProjectWhenDeletedTwiceErrorShouldBeThrown() throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assert projects != null;
        assertEquals(1, projects.length);

        String contentAsString2 =
                mvc.perform(
                                MockMvcRequestBuilders.delete(
                                                String.format(
                                                        "%s/%s", baseUrl, projects[0].getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()) // no content
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.delete(
                                                String.format(
                                                        "%s/%s", baseUrl, projects[0].getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("No project found for id: " + projects[0].getId(), errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test009_ProjectControllerWhenReceivesValidProjectUpdateDTOShouldReturnHttpOkTest()
            throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String titleforUpdate = "New Project Title";
        String descriptionforUpdate = "New Project description";
        List<String> linksForUpdate = new ArrayList<>();
        linksForUpdate.add("http://google.com");
        linksForUpdate.add("http://test.com");
        List<String> tagsForUpdate = new ArrayList<>();
        tagsForUpdate.add("tag1");
        tagsForUpdate.add("tag2");
        List<String> languagesForUpdate = Arrays.asList("Python", "PHP");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        ProjectUpdateDTO projectUpdateDTO =
                ProjectUpdateDTO.builder()
                        .title(titleforUpdate)
                        .description(descriptionforUpdate)
                        .links(linksForUpdate)
                        .tags(tagsForUpdate)
                        .languages(languagesForUpdate)
                        .build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project project = objectMapper.readValue(contentAsString, Project.class);

        String updatedContentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(
                                                String.format("%s/%s", baseUrl, project.getId()))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project updatedProject = objectMapper.readValue(updatedContentAsString, Project.class);

        assert updatedProject != null;
        assertEquals(projectUpdateDTO.getTitle(), updatedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), updatedProject.getDescription());
    }

    /** Given valid ID Should return */
    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test010_ProjectControllerWhenReceivesValidIdShouldReturnHttpOkTest() throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project project = objectMapper.readValue(contentAsString, Project.class);

        String contentAsString2 =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format("%s/%s", baseUrl, Objects.requireNonNull(project.getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project projectDetails = objectMapper.readValue(contentAsString2, Project.class);

        assert projectDetails != null;
        assertEquals(
                projectToCreate.getOwner().getNickname(), projectDetails.getOwner().getNickname());
        assertEquals(projectToCreate.getTitle(), projectDetails.getTitle());
        assertEquals(projectToCreate.getDescription(), projectDetails.getDescription());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test011_GivenAnExistingProjectWhenGettingAllProjectsThenItIsReturned() throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "description";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);

        assert projects != null;
        assertEquals(1, projects.length);
        assertEquals(title, projects[0].getTitle());
        assertEquals(owner.getNickname(), projects[0].getOwner().getNickname());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test012_ProjectControllerWhenReceiveCreateProjectDTOWithTagShorterThanOneCharacterShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link2.com");
        List<String> tags = Arrays.asList("", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Tag name must be between 1 and 24 characters", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test013_ProjectControllerWhenReceiveCreateProjectDTOWithTagLargerThanTwentyfourCharactersShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link2.com");
        List<String> tags = Arrays.asList("This is not a valid tag for a project", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Tag name must be between 1 and 24 characters", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test014_ProjectControllerWhenReceiveCreateProjectDTOWithNoLinksShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = List.of();
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Number of links must be between 1 and 5", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test015_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanFiveLinksShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links =
                Arrays.asList(
                        "http://link1.com",
                        "http://link2.com",
                        "http://link3.com",
                        "http://link4.com",
                        "http://link5.com",
                        "http://link6.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Number of links must be between 1 and 5", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test016_ProjectControllerSuccesfulSearch() throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links =
                Arrays.asList("http://link1.com", "http://link2.com", "http://link3.com");
        List<String> secondLinks =
                Arrays.asList("http://link4.com", "http://link5.com", "http://link6.com");
        List<String> thirdLinks =
                Arrays.asList("http://link7.com", "http://link8.com", "http://link9.com");
        List<String> fourthLinks =
                Arrays.asList("http://link10.com", "http://link11.com", "http://link12.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthLlanguages = Arrays.asList("TypeScript", "C");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .languages(secondLanguages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .languages(thirdLanguages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .languages(fourthLlanguages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(firstProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(secondProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(thirdProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fourthProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get("/project/search?name=pro&page=0")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(4, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test017_ProjectControllerSuccesfulEmptySearch() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links =
                Arrays.asList("http://link1.com", "http://link2.com", "http://link3.com");
        List<String> secondLinks =
                Arrays.asList("http://link4.com", "http://link5.com", "http://link6.com");
        List<String> thirdLinks =
                Arrays.asList("http://link7.com", "http://link8.com", "http://link9.com");
        List<String> fourthLinks =
                Arrays.asList("http://link10.com", "http://link11.com", "http://link12.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthLlanguages = Arrays.asList("TypeScript", "C");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .languages(secondLanguages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .languages(thirdLanguages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .languages(fourthLlanguages)
                        .owner(owner)
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(firstProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(secondProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(thirdProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(fourthProjectToCreate))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get("/project/search?name=pro&page=1")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(0, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test018_WhenGettingValidLanguagesNameListShouldBeReturned() throws Exception {
        String validLanguageNames =
                "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";
        List<String> validLanguageList =
                new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get("/project/languages")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String[] languages = objectMapper.readValue(contentAsString, String[].class);

        assertNotNull(languages);
        assertEquals(validLanguageList, Arrays.asList(languages));
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test019_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLanguageShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://google.com", "http://link.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Not Valid Language", "C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Language Not Valid Language is not valid", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test020_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanThreeLanguagesShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C", "Python", "PHP");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("languages: Number of languages must be between 1 and 3\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test020_ProjectControllerWhenReceiveCreateProjectDTOWithNoLanguagesShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.post(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToCreate))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("languages: Number of languages must be between 1 and 3\n", errorMessage);
    }

    @Test
    void
            Test021_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLinkShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "link");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of("C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("links[1]: Invalid pattern for field\n", getResponse.getBody());
    }

    @Test
    void
            Test022_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedLinkShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link1.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of("C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("links: must only contain unique elements\n", getResponse.getBody());
    }

    @Test
    void
            Test023_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedTagsShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com");
        List<String> tags = Arrays.asList("t", "t");
        List<String> languages = List.of("C");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("tags: must only contain unique elements\n", getResponse.getBody());
    }

    @Test
    void Test024_ProjectControllerWhenAskedForTagsShouldReturnAllTags() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links =
                Arrays.asList("http://link1.com", "http://link2.com", "http://link3.com");
        List<String> secondLinks =
                Arrays.asList("http://link4.com", "http://link5.com", "http://link6.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
                        .owner(owner)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .languages(secondLanguages)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> createFirstProject = new HttpEntity<>(firstProjectToCreate);
        HttpEntity<ProjectCreateDTO> createSecondProject = new HttpEntity<>(secondProjectToCreate);

        val postFirstResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createFirstProject, Project.class);
        assertEquals(HttpStatus.CREATED, postFirstResponse.getStatusCode());
        val postSecondResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createSecondProject, Project.class);
        assertEquals(HttpStatus.CREATED, postSecondResponse.getStatusCode());

        val getResponse =
                restTemplate.exchange("/project/tags", HttpMethod.GET, null, String[].class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        String[] t = getResponse.getBody();
        String[] expectedTags = {"tag1", "tag2", "tag3", "tag4"};
        assertEquals(4, t.length);
        assertArrayEquals(expectedTags, t);
    }
}
