package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import com.a2.backend.repository.DiscussionRepository;
import com.a2.backend.repository.ProjectRepository;
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
    @Autowired private DiscussionRepository discussionRepository;
    @Autowired private ProjectRepository projectRepository;

    private final String baseUrl = "/project";

    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@gmail.com")
                    .biography("bio")
                    .password("password")
                    .build();
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
    String titleforUpdate = "New Project Title";
    String descriptionforUpdate = "New Project description";
    List<String> linksForUpdate = List.of("http://google.com", "http://test.com");
    List<String> tagsForUpdate = List.of("tag1", "tag2");
    List<String> languagesForUpdate = Arrays.asList("Python", "PHP");

    ProjectUpdateDTO projectUpdateDTO =
            ProjectUpdateDTO.builder()
                    .title(titleforUpdate)
                    .description(descriptionforUpdate)
                    .links(linksForUpdate)
                    .tags(tagsForUpdate)
                    .forumTags(forumTags)
                    .languages(languagesForUpdate)
                    .build();

    String discussionTitle = "Discussion title";
    List<String> discussionTags = Arrays.asList("desctag1", "desctag2");

    DiscussionCreateDTO discussionCreateDTO =
            DiscussionCreateDTO.builder()
                    .forumTags(discussionTags)
                    .title(discussionTitle)
                    .body("aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                    .build();

    public List<ProjectCreateDTO> projectCreator(int numberOfProjects) {
        List<ProjectCreateDTO> projects = new ArrayList<>();
        int i = 0;
        while (i < numberOfProjects) {
            ProjectCreateDTO projectCreated =
                    ProjectCreateDTO.builder()
                            .title(title + " " + i)
                            .description(description + " " + i)
                            .links(links)
                            .tags(tags)
                            .forumTags(forumTags)
                            .languages(languages)
                            .build();
            projects.add(projectCreated);
            i++;
        }
        return projects;
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test001_ProjectControllerWhenReceivesValidCreateProjectDTOShouldReturnStatusCreated()
            throws Exception {
        userRepository.save(owner);

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
        projectToCreate.setTitle(title);

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

        String description = "Short";
        projectToCreate.setDescription(description);

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
        projectToCreate.setDescription(description);
        projectToCreate.setTitle(title);

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
        List<String> tags = Arrays.asList("", "tag2");
        projectToCreate.setTags(tags);
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
        List<String> tags = Arrays.asList("This is not a valid tag for a project", "tag2");
        projectToCreate.setTags(tags);

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
        List<String> links = List.of();
        projectToCreate.setLinks(links);
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

        List<String> links =
                Arrays.asList(
                        "http://link1.com",
                        "http://link2.com",
                        "http://link3.com",
                        "http://link4.com",
                        "http://link5.com",
                        "http://link6.com");
        projectToCreate.setLinks(links);

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
    void Test016_WhenGettingValidLanguagesNameListShouldBeReturned() throws Exception {
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
            Test017_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLanguageShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);
        List<String> languages = Arrays.asList("Not Valid Language", "C");
        projectToCreate.setLanguages(languages);
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
            Test018_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanThreeLanguagesShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);
        List<String> languages = Arrays.asList("Java", "C", "Python", "PHP");
        projectToCreate.setLanguages(languages);

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
            Test019_ProjectControllerWhenReceiveCreateProjectDTOWithNoLanguagesShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);
        List<String> languages = List.of();
        projectToCreate.setLanguages(languages);

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
            Test020_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLinkShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);

        List<String> links = Arrays.asList("http://link1.com", "link");
        projectToCreate.setLinks(links);
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
            Test021_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedLinkShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);
        List<String> links = Arrays.asList("http://link1.com", "http://link1.com");
        projectToCreate.setLinks(links);

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
            Test022_ProjectControllerWhenReceiveCreateProjectDTOWithRepeatedTagsShouldReturnStatusBadRequest()
                    throws Exception {
        userRepository.save(owner);
        List<String> tags = Arrays.asList("t", "t");
        projectToCreate.setTags(tags);

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
    void Test023_ProjectControllerWhenAskedForTagsShouldReturnAllTags() throws Exception {
        userRepository.save(owner);

        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");

        List<ProjectCreateDTO> projects = projectCreator(2);
        projects.get(0).setFeatured(true);
        projects.get(0).setTags(tags);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setTags(secondTags);

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
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
    void Test024_ProjectControllerSuccessfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);

        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> secondTags = Arrays.asList("tag3", "tag4");
        List<String> thirdTags = Arrays.asList("tag5", "tag6");
        List<String> fourthTags = Arrays.asList("tag7", "tag8");
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");

        List<ProjectCreateDTO> projects = projectCreator(4);
        projects.get(0).setFeatured(true);
        projects.get(0).setLanguages(languages);
        projects.get(0).setTags(tags);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setLanguages(secondLanguages);
        projects.get(1).setTags(secondTags);
        projects.get(2).setLanguages(thirdLanguages);
        projects.get(2).setTags(thirdTags);
        projects.get(3).setFeatured(true);
        projects.get(3).setLanguages(fourthlanguages);
        projects.get(3).setTags(fourthTags);

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .title("PrOjEct")
                        .tags(Arrays.asList("tag8"))
                        .languages(Arrays.asList("TypeScript"))
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(1, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test025_ProjectControllerSuccessfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");

        List<ProjectCreateDTO> projects = projectCreator(4);
        projects.get(0).setFeatured(true);
        projects.get(0).setLanguages(languages);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setLanguages(secondLanguages);
        projects.get(2).setLanguages(thirdLanguages);
        projects.get(3).setFeatured(true);
        projects.get(3).setLanguages(fourthlanguages);

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .title("Project")
                        .languages(Arrays.asList("TypeScript"))
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(1, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test026_ProjectControllerSuccessfulMultiFilterSearch() throws Exception {
        userRepository.save(owner);
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");

        List<ProjectCreateDTO> projects = projectCreator(4);
        projects.get(0).setFeatured(true);
        projects.get(0).setLanguages(languages);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setLanguages(secondLanguages);
        projects.get(2).setLanguages(thirdLanguages);
        projects.get(3).setFeatured(true);
        projects.get(3).setLanguages(fourthlanguages);

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder().languages(Arrays.asList("JavAScript")).build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(1, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test027_ProjectControllerSuccessfulMultiFilterSearchWithFeatured() throws Exception {
        userRepository.save(owner);
        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");

        List<ProjectCreateDTO> projects = projectCreator(4);
        projects.get(0).setFeatured(true);
        projects.get(0).setLanguages(languages);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setLanguages(secondLanguages);
        projects.get(2).setLanguages(thirdLanguages);
        projects.get(3).setFeatured(true);
        projects.get(3).setLanguages(fourthlanguages);

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .featured(true)
                        .languages(Arrays.asList("TypeScript"))
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(1, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test028_ProjectControllerSuccessfulMultiFilterSearchWithInsensitiveCase()
            throws Exception {
        userRepository.save(owner);

        List<String> languages = Arrays.asList("Java", "C");
        List<String> secondLanguages = Arrays.asList("Java", "Python");
        List<String> thirdLanguages = Arrays.asList("JavaScript", "C#");
        List<String> fourthlanguages = Arrays.asList("TypeScript", "C");

        List<ProjectCreateDTO> projects = projectCreator(4);
        projects.get(0).setFeatured(true);
        projects.get(0).setLanguages(languages);
        projects.get(1).setTitle("Not Start Project");
        projects.get(1).setLanguages(secondLanguages);
        projects.get(2).setLanguages(thirdLanguages);
        projects.get(3).setFeatured(true);
        projects.get(3).setLanguages(fourthlanguages);

        ProjectSearchDTO projectToSearch =
                ProjectSearchDTO.builder()
                        .featured(true)
                        .languages(Arrays.asList("scrIpT"))
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projects.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(0, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test029_ProjectControllerWhenReceivesValidCreateDiscussionDTOShouldReturnStatusCreated()
            throws Exception {
        userRepository.save(owner);

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

        DiscussionDTO discussion = objectMapper.readValue(discussionAsString, DiscussionDTO.class);
        assertNotNull(discussion.getId());
        assertEquals(discussionTitle, discussion.getTitle());
        assertEquals(discussion.getForumTags().size(), discussionCreateDTO.getForumTags().size());
        assertEquals(
                discussion.getForumTags().get(0).getName(),
                discussionCreateDTO.getForumTags().get(0));
        assertEquals(discussion.getProject().getId(), project.getId());
        assertTrue(!discussionRepository.findById(discussion.getId()).isEmpty());
        Project project1 = projectRepository.findByTitle("Project title").get();
        assertEquals(project1.getDiscussions().get(0).getTitle(), "Discussion title");
        assertEquals(
                project1.getDiscussions().get(0).getBody(),
                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka");
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test030_ProjectControllerWhenReceivesNotValidProjectWhileCreatingDiscussionShouldReturnBadRequestStatus()
                    throws Exception {
        userRepository.save(owner);
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
            Test031_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNonExistingProjectShouldReturnBadStatus()
                    throws Exception {
        userRepository.save(owner);
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
            Test032_ProjectControllerWhenReceivesSecondValidCreateDiscussionDTOShouldReturnStatusCreated()
                    throws Exception {
        userRepository.save(owner);

        DiscussionCreateDTO secondDiscussionCreateDTO =
                DiscussionCreateDTO.builder()
                        .forumTags(discussionTags)
                        .body(
                                "aaaaaaaaaaaaaakakakakakkakakakakakakakakakakakakkakakakakakakakakakaka")
                        .title(discussionTitle + "2")
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
            Test033_ProjectControllerWhenReceivesValidCreateDiscussionDTOButNotExistingProjectShouldReturnBadRequest()
                    throws Exception {
        userRepository.save(owner);

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

    @Test
    @WithMockUser(username = "some@gmail.com")
    void Test034_ProjectControllerSuccessfulMultiFilterSearchWithInsensitiveCase()
            throws Exception {
        userRepository.save(owner);

        List<ProjectCreateDTO> projectCreateDTOS = projectCreator(4);
        projectCreateDTOS.get(1).setTitle("Not Start Project");
        ProjectSearchDTO projectToSearch = ProjectSearchDTO.builder().title("nOT").build();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectCreateDTOS.get(0)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectCreateDTOS.get(1)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectCreateDTOS.get(2)))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        mvc.perform(
                        MockMvcRequestBuilders.post(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(projectCreateDTOS.get(3)))
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

        SearchResultDTO searchResultDTO =
                objectMapper.readValue(contentAsString, SearchResultDTO.class);
        assertEquals(1, searchResultDTO.getProjects().size());
        assertTrue(searchResultDTO.getPageAmount() == 0);
        assertEquals("Not Start Project", searchResultDTO.getProjects().get(0).getTitle());
    }

    @Test
    void Test_ProjectCreator() {
        List<ProjectCreateDTO> projects = projectCreator(3);

        assertEquals(3, projects.size());
    }
}
// I was able to reduce the amount of lines from 2200+ to 1545
