package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectControllerTest {

    @Autowired private TestRestTemplate restTemplate;

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
    void Test001_ProjectControllerWhenReceivesValidCreateProjectDTOShouldReturnStatusCreated() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());
    }

    @Test
    void
            Test002_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidTitleShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "a";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assert (Objects.requireNonNull(getResponse.getBody()).contains("title"));
    }

    @Test
    void
            Test003_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Short";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assert (Objects.requireNonNull(getResponse.getBody()).contains("description"));
    }

    @Test
    void
            Test004_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionAndTitleShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "a";
        String description = "Short";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assert (Objects.requireNonNull(getResponse.getBody()).contains("description"));
        assert getResponse.getBody().contains("title");
    }

    @Test
    void Test005_GivenNoExistingProjectsWhenGettingAllProjectsThenEmptyResponseIsReturned() {
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(0, Objects.requireNonNull(getResponse.getBody()).length);
    }

    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenProjectIdIsReturned() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assert projects != null;
        assertEquals(1, projects.length);

        val deleteResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, projects[0].getId()),
                        HttpMethod.DELETE,
                        null,
                        UUID.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }

    @Test
    void Test007_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assert projects != null;
        assertEquals(1, projects.length);

        val deleteResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, projects[0].getId()),
                        HttpMethod.DELETE,
                        null,
                        UUID.class);
        assertEquals(deleteResponse.getStatusCode(), HttpStatus.NO_CONTENT);

        val getResponse1 = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse1.getStatusCode());
        Project[] projects1 = getResponse1.getBody();

        assert projects1 != null;
        assertEquals(0, projects1.length);
    }

    @Test
    void Test008_GivenASingleExistingProjectWhenDeletedTwiceErrorShouldBeThrown() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assert projects != null;
        assertEquals(1, projects.length);

        ResponseEntity<UUID> deleteResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, projects[0].getId()),
                        HttpMethod.DELETE,
                        null,
                        UUID.class);
        assertEquals(deleteResponse.getStatusCode(), HttpStatus.NO_CONTENT);

        val deleteResponse1 =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, projects[0].getId()),
                        HttpMethod.DELETE,
                        null,
                        String.class);
        assertEquals(HttpStatus.BAD_REQUEST, deleteResponse1.getStatusCode());
        assertEquals("No project found for id: " + projects[0].getId(), deleteResponse1.getBody());
    }

    @Test
    void Test009_ProjectControllerWhenReceivesValidProjectUpdateDTOShouldReturnHttpOkTest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> languages = Arrays.asList("Java", "C");

        String titleforUpdate = "New Project Title";
        String descriptionforUpdate = "New Project description";
        List<String> linksForUpdate = new ArrayList<>();
        linksForUpdate.add("link1");
        linksForUpdate.add("link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);
        HttpEntity<ProjectUpdateDTO> requestUpdate = new HttpEntity<>(projectUpdateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());

        val updatedResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, postResponse.getBody().getId()),
                        HttpMethod.PUT,
                        requestUpdate,
                        Project.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        Project project = updatedResponse.getBody();
        assertNotNull(project);

        assert project != null;
        assertEquals(projectUpdateDTO.getTitle(), project.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), project.getDescription());
    }

    /** Given valid ID Should return */
    @Test
    void Test010_ProjectControllerWhenReceivesValidIdShouldReturnHttpOkTest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());

        val getProjectDetailsResponse =
                restTemplate.exchange(
                        String.format(
                                "%s/%s",
                                baseUrl, Objects.requireNonNull(postResponse.getBody()).getId()),
                        HttpMethod.GET,
                        null,
                        Project.class);
        assertEquals(HttpStatus.OK, getProjectDetailsResponse.getStatusCode());

        Project project = getProjectDetailsResponse.getBody();

        assert project != null;
        assertEquals(projectToCreate.getOwner().getEmail(), project.getOwner().getEmail());
        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
    }

    @Test
    void Test011_GivenAnExistingProjectWhenGettingAllProjectsThenItIsReturned() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "description";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Project[] projects = getResponse.getBody();
        assert projects != null;
        assertEquals(1, projects.length);
        assertEquals(title, projects[0].getTitle());
        assertEquals(owner.getNickname(), projects[0].getOwner().getNickname());
    }

    @Test
    void
            Test012_ProjectControllerWhenReceiveCreateProjectDTOWithTagShorterThanOneCharacterShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test013_ProjectControllerWhenReceiveCreateProjectDTOWithTagLargerThanTwentyfourCharactersShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test014_ProjectControllerWhenReceiveCreateProjectDTOWithNoLinksShouldReturnStatusBadRequest() {
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test015_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanFiveLinksShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2", "link3", "link4", "link5", "link6");
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
        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test016_ProjectControllerSuccesfulSearch() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2", "link3");
        List<String> secondLinks = Arrays.asList("link4", "link5", "link6");
        List<String> thirdLinks = Arrays.asList("link7", "link8", "link9");
        List<String> fourthLinks = Arrays.asList("link10", "link11", "link12");
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

        HttpEntity<ProjectCreateDTO> createFourthProject = new HttpEntity<>(fourthProjectToCreate);
        HttpEntity<ProjectCreateDTO> createFirstProject = new HttpEntity<>(firstProjectToCreate);
        HttpEntity<ProjectCreateDTO> createSecondProject = new HttpEntity<>(secondProjectToCreate);
        HttpEntity<ProjectCreateDTO> createThirdProject = new HttpEntity<>(thirdProjectToCreate);

        val postFirstResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createFirstProject, Project.class);
        assertEquals(HttpStatus.CREATED, postFirstResponse.getStatusCode());
        val postSecondResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createSecondProject, Project.class);
        assertEquals(HttpStatus.CREATED, postSecondResponse.getStatusCode());
        val postThirdResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createThirdProject, Project.class);
        assertEquals(HttpStatus.CREATED, postThirdResponse.getStatusCode());
        val postFourthResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createFourthProject, Project.class);
        assertEquals(HttpStatus.CREATED, postThirdResponse.getStatusCode());

        val getResponse =
                restTemplate.exchange(
                        "/project/search?name=pro&page=0", HttpMethod.GET, null, Project[].class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Project[] projects = getResponse.getBody();
        assertEquals(4, projects.length);
    }

    @Test
    void Test017_ProjectControllerSuccesfulEmptySearch() {
        userRepository.save(owner);
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2", "link3");
        List<String> secondLinks = Arrays.asList("link4", "link5", "link6");
        List<String> thirdLinks = Arrays.asList("link7", "link8", "link9");
        List<String> fourthLinks = Arrays.asList("link10", "link11", "link12");
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

        HttpEntity<ProjectCreateDTO> createFourthProject = new HttpEntity<>(fourthProjectToCreate);
        HttpEntity<ProjectCreateDTO> createFirstProject = new HttpEntity<>(firstProjectToCreate);
        HttpEntity<ProjectCreateDTO> createSecondProject = new HttpEntity<>(secondProjectToCreate);
        HttpEntity<ProjectCreateDTO> createThirdProject = new HttpEntity<>(thirdProjectToCreate);

        val postFirstResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createFirstProject, Project.class);
        assertEquals(HttpStatus.CREATED, postFirstResponse.getStatusCode());
        val postSecondResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createSecondProject, Project.class);
        assertEquals(HttpStatus.CREATED, postSecondResponse.getStatusCode());
        val postThirdResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createThirdProject, Project.class);
        assertEquals(HttpStatus.CREATED, postThirdResponse.getStatusCode());
        val postFourthResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, createFourthProject, Project.class);
        assertEquals(HttpStatus.CREATED, postThirdResponse.getStatusCode());

        val getResponse =
                restTemplate.exchange(
                        "/project/search?name=pro&page=1", HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Project[] projects = getResponse.getBody();
        assertEquals(0, projects.length);
    }

    @Test
    void Test018_WhenGettingValidLanguagesNameListShouldBeReturned() {
        String validLanguageNames =
                "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";
        List<String> validLanguageList =
                new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

        val getResponse =
                restTemplate.exchange("/project/languages", HttpMethod.GET, null, String[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        String[] languages = getResponse.getBody();

        assertNotNull(languages);
        assertEquals(validLanguageList, Arrays.asList(languages));
    }

    @Test
    void
            Test019_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidLanguageShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Language Not Valid Language is not valid", getResponse.getBody());
    }

    @Test
    void
            Test020_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanThreeLanguagesShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals(
                "languages: Number of languages must be between 1 and 3\n", getResponse.getBody());
    }

    @Test
    void
            Test020_ProjectControllerWhenReceiveCreateProjectDTOWithNoLanguagesShouldReturnStatusBadRequest() {
        userRepository.save(owner);

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
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

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals(
                "languages: Number of languages must be between 1 and 3\n", getResponse.getBody());
    }
}
