package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.DiscussionCreateDTO;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
class ProjectControllerTest extends AbstractTest {

    @Autowired MockMvc mvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;

    private final String baseUrl = "/project";

    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@gmail.com")
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
        List<String> forumTags = Arrays.asList("help", "fix");
        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
                        .andExpect(status().isNoContent())
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
                        .andExpect(status().isNoContent())
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
                        .andExpect(status().isNoContent())
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
        List<String> forumTags = Arrays.asList("help", "fix");

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
                        .forumTags(forumTags)
                        .languages(languages)
                        .build();

        ProjectUpdateDTO projectUpdateDTO =
                ProjectUpdateDTO.builder()
                        .title(titleforUpdate)
                        .description(descriptionforUpdate)
                        .links(linksForUpdate)
                        .tags(tagsForUpdate)
                        .forumTags(forumTags)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
                                                String.format(
                                                        "%s/%s",
                                                        baseUrl,
                                                        Objects.requireNonNull(project.getId())))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project projectDetails = objectMapper.readValue(contentAsString2, Project.class);

        assert projectDetails != null;
        assertEquals(owner.getNickname(), projectDetails.getOwner().getNickname());
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assert (Objects.requireNonNull(errorMessage).contains("tags"));
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assert (Objects.requireNonNull(errorMessage).contains("tags"));
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assert (Objects.requireNonNull(errorMessage).contains("links"));
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assert (Objects.requireNonNull(errorMessage).contains("links"));
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
            Test021_ProjectControllerWhenReceiveCreateProjectDTOWithNoLanguagesShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of();
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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
            Test021_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLinkShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "link");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of("C");
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assertEquals("links[1]: Invalid pattern for field\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test022_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedLinkShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com", "http://link1.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = List.of("C");
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assertEquals("links: must only contain unique elements\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test023_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedTagsShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link1.com");
        List<String> tags = Arrays.asList("t", "t");
        List<String> languages = List.of("C");
        List<String> forumTags = Arrays.asList("help", "fix");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
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

        assertEquals("tags: must only contain unique elements\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test024_ProjectControllerWhenAskedForTagsShouldReturnAllTags() throws Exception {
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
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");

        ProjectCreateDTO firstProjectToCreate =
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
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
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

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get("/project/tags")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String[] t = objectMapper.readValue(contentAsString, String[].class);
        String[] expectedTags = {"tag1", "tag2", "tag3", "tag4"};
        assertEquals(4, t.length);
        assertArrayEquals(expectedTags, t);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test022_ProjectControllerSuccesfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> secondLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> thirdLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> fourthLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthLlanguages = Arrays.asList("TypeScript", "C");
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");
        List<String> forumTags3 = Arrays.asList("help3", "fix3");
        List<String> forumTags4 = Arrays.asList("help4", "fix4");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .links(links)
                        .description(description)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .forumTags(forumTags3)
                        .languages(thirdLanguages)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .forumTags(forumTags4)
                        .languages(fourthLlanguages)
                        .build();

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .title("PrOjEct")
                        .tags(Arrays.asList("tag8"))
                        .languages(Arrays.asList("TypeScript"))
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

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test023_ProjectControllerSuccesfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> secondLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> thirdLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> fourthLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");
        List<String> forumTags3 = Arrays.asList("help3", "fix3");
        List<String> forumTags4 = Arrays.asList("help4", "fix4");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .tags(tags)
                        .forumTags(forumTags)
                        .links(links)
                        .languages(languages)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .forumTags(forumTags3)
                        .languages(thirdLanguages)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .forumTags(forumTags4)
                        .languages(fourthlanguages)
                        .build();

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .title("Project")
                        .languages(Arrays.asList("TypeScript"))
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
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(1, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test024_ProjectControllerSuccesfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> secondLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> thirdLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> fourthLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");
        List<String> forumTags3 = Arrays.asList("help3", "fix3");
        List<String> forumTags4 = Arrays.asList("help4", "fix4");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .tags(tags)
                        .forumTags(forumTags)
                        .links(links)
                        .languages(languages)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .forumTags(forumTags3)
                        .languages(thirdLanguages)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .forumTags(forumTags4)
                        .languages(fourthlanguages)
                        .build();

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("JavAScript")).build();

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
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(1, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test026_ProjectControllerSuccesfulMultiFilterSearchWithFeatured() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> secondLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> thirdLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> fourthLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");
        List<String> forumTags3 = Arrays.asList("help3", "fix3");
        List<String> forumTags4 = Arrays.asList("help4", "fix4");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .tags(tags)
                        .forumTags(forumTags)
                        .links(links)
                        .languages(languages)
                        .featured(true)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .forumTags(forumTags3)
                        .languages(thirdLanguages)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .forumTags(forumTags4)
                        .featured(true)
                        .languages(fourthlanguages)
                        .build();

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .featured(true)
                        .languages(Arrays.asList("TypeScript"))
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
                                MockMvcRequestBuilders.post("/project/search")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(projectToSearch))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Project[] projects = objectMapper.readValue(contentAsString, Project[].class);
        assertEquals(1, projects.length);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test027_ProjectControllerSuccesfulMultiFilterSearchWithInsensitiveCase() throws Exception {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> secondLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> thirdLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> fourthLinks = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");
        List<String> forumTags = Arrays.asList("help", "fix");
        List<String> forumTags2 = Arrays.asList("help2", "fix2");
        List<String> forumTags3 = Arrays.asList("help3", "fix3");
        List<String> forumTags4 = Arrays.asList("help4", "fix4");

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .tags(tags)
                        .forumTags(forumTags)
                        .links(links)
                        .languages(languages)
                        .featured(true)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .forumTags(forumTags2)
                        .languages(secondLanguages)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .forumTags(forumTags3)
                        .languages(thirdLanguages)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
                        .forumTags(forumTags4)
                        .featured(true)
                        .languages(fourthlanguages)
                        .build();

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .featured(true)
                        .languages(Arrays.asList("scrIpT"))
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

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test0028_ProjectControllerWhenReceivesValidCreateDiscussionDTOShouldReturnStatusCreated()
            throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
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

        Discussion discussion = objectMapper.readValue(discussionAsString, Discussion.class);
        assertNotNull(discussion.getId());
        assertEquals(discussionTitle, discussion.getTitle());
        assertEquals(discussion.getForumTags().size(), discussionCreateDTO.getForumTags().size());
        assertEquals(
                discussion.getForumTags().get(0).getName(),
                discussionCreateDTO.getForumTags().get(0));
        assertEquals(discussion.getProject().getId(), project.getId());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test0029_ProjectControllerWhenReceivesNotValidProjectWhileCreatingDiscussionShouldReturnBadRequestStatus()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .forumTags(discussionTags)
                        .tags(tags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
                                                        + 1
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test0030_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNonExistingProjectShouldReturnBadStatus()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .forumTags(discussionTags)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
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

        Discussion discussion = objectMapper.readValue(discussionAsString, Discussion.class);
        assertNotNull(discussion.getId());
        assertEquals(discussionTitle, discussion.getTitle());
        assertEquals(discussion.getForumTags().size(), discussionCreateDTO.getForumTags().size());
        assertEquals(
                discussion.getForumTags().get(0).getName(),
                discussionCreateDTO.getForumTags().get(0));
        assertEquals(discussion.getProject().getId(), project.getId());

        String secondDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test0031_ProjectControllerWhenReceivesSecondValidCreateDiscussionDTOShouldReturnStatusCreated()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();
        DiscussionCreateDTO secondDiscussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle + "2")
                        .build();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .forumTags(discussionTags)
                        .tags(tags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
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

        Discussion discussion = objectMapper.readValue(discussionAsString, Discussion.class);
        assertNotNull(discussion.getId());
        assertEquals(discussionTitle, discussion.getTitle());
        assertEquals(discussion.getForumTags().size(), discussionCreateDTO.getForumTags().size());
        assertEquals(
                discussion.getForumTags().get(0).getName(),
                discussionCreateDTO.getForumTags().get(0));
        assertEquals(discussion.getProject().getId(), project.getId());

        String secondDiscussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        secondDiscussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Discussion secondDiscussion =
                objectMapper.readValue(secondDiscussionAsString, Discussion.class);
        assertNotNull(secondDiscussion.getId());
        assertEquals(discussionTitle + "2", secondDiscussion.getTitle());
        assertEquals(
                secondDiscussion.getForumTags().size(),
                secondDiscussionCreateDTO.getForumTags().size());
        assertEquals(
                secondDiscussion.getForumTags().get(0).getName(),
                secondDiscussionCreateDTO.getForumTags().get(0));
        assertEquals(secondDiscussion.getProject().getId(), project.getId());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test0032_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNotExistingProjectShouldReturnBadRequest()
                    throws Exception {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("http://link.com", "http://link2.com");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String discussionTitle = "Discussion title";
        List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

        DiscussionCreateDTO discussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(discussionTitle)
                        .build();

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .forumTags(discussionTags)
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .languages(languages)
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
        assertEquals(owner.getId(), project.getOwner().getId());

        String discussionAsString =
                mvc.perform(
                                MockMvcRequestBuilders.post(
                                                "/project/"
                                                        + project.getId().toString()
                                                        + "z"
                                                        + "/discussion")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        discussionCreateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }
}
