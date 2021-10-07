package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.Language;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.Tag;
import com.a2.backend.entity.User;
import com.a2.backend.exception.ProjectNotFoundException;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectSearchDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.LanguageService;
import com.a2.backend.service.ProjectService;
import com.a2.backend.service.TagService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectServiceImplTest extends AbstractTest {

    @Autowired private ProjectService projectService;

    @Autowired private TagService tagService;

    @Autowired private LanguageService languageService;

    @Autowired private ProjectRepository projectRepository;

    @Autowired private UserRepository userRepository;

    String title = "Project title";
    String description = "Testing exception for existing title";
    List<String> links = Arrays.asList("link1", "link2");
    List<String> tags = Arrays.asList("tag1", "tag2");
    List<String> languages = Arrays.asList("Java", "C");
    List<String> forumTags = Arrays.asList("help", "actual");

    User owner =
            User.builder()
                    .nickname("nickname")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .preferredLanguages(List.of("Java", "C"))
                    .preferredTags(List.of("tag1", "tag3"))
                    .build();

    static List<String> linksUpdate = new ArrayList<>();
    static List<String> tagsUpdate = new ArrayList<>();
    static List<String> forumTagsUpdate = new ArrayList<>();
    List<String> languagesUpdate = Arrays.asList("Java", "Ruby");

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
                    .title("new title")
                    .links(linksUpdate)
                    .tags(tagsUpdate)
                    .forumTags(forumTagsUpdate)
                    .languages(languagesUpdate)
                    .description("new description")
                    .build();

    @BeforeAll
    static void setUp() {
        linksUpdate.add("http://google.com");
        tagsUpdate.add("tag1");
        tagsUpdate.add("tag4");
        forumTagsUpdate.add("forumTagUpdate");
        forumTagsUpdate.add("forumTagUpdate2");
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test001_ProjectServiceWhenReceivesValidCreateProjectDTOShouldCreateProject() {
        userRepository.save(owner);

        assertTrue(projectService.getAllProjects().isEmpty());

        Project projectCreated = projectService.createProject(projectToCreate);

        val projects = projectService.getAllProjects();

        assertFalse(projects.isEmpty());
        assertEquals(1, projects.size());

        val project = projects.get(0);

        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
        assertEquals(tagService.findTagsByNames(projectToCreate.getTags()), project.getTags());
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                project.getLanguages());
        assertEquals(projectToCreate.getLinks(), project.getLinks());

        assertEquals(owner.getId(), project.getOwner().getId());
        assertEquals(owner.getNickname(), project.getOwner().getNickname());
        assertEquals(owner.getEmail(), project.getOwner().getEmail());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test002_ProjectServiceWhenReceivesCreateProjectDTOWithExistingTitleShouldThrowException() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "Project title";
        String description2 = "Testing exception for existing title";

        ProjectCreateDTO projectToCreateWithRepeatedTitle =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description2)
                        .links(links)
                        .tags(tags)
                        .forumTags(forumTags)
                        .languages(languages)
                        .build();

        assertThrows(
                ProjectWithThatTitleExistsException.class,
                () -> projectService.createProject(projectToCreateWithRepeatedTitle));
    }

    @Test
    void Test004_ProjectListWithNoSavedProjectsShouldBeEmpty() {
        assertTrue(projectService.getAllProjects().isEmpty());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test005_ProjectListWithSavedProjectsShouldContainProjects() {
        userRepository.save(owner);

        assertTrue(projectService.getAllProjects().isEmpty());

        Project savedProject = projectService.createProject(projectToCreate);

        val allProjects = projectService.getAllProjects();

        assertEquals(1, allProjects.size());

        val singleProject = allProjects.get(0);

        assertEquals(projectToCreate.getTitle(), singleProject.getTitle());
        assertEquals(projectToCreate.getDescription(), singleProject.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreate.getTags()), singleProject.getTags());
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                singleProject.getLanguages());
        assertEquals(projectToCreate.getLinks(), singleProject.getLinks());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test006_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {
        userRepository.save(owner);

        // Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project = projectService.createProject(projectToCreate);
        val allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        // When
        projectService.deleteProject(project.getId());

        // Then
        assertTrue(projectService.getAllProjects().isEmpty());
    }

    /** Given non existent id when projectService.updateProject then throw NotFoundException */
    @Test
    void
            Test007_ProjectServiceWhenRecievesNonExistentProjectIDShouldThrowProjectNotFoundException() {
        UUID nonexistentID = UUID.randomUUID();
        assertTrue(projectRepository.findById(nonexistentID).isEmpty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.updateProject(projectUpdateDTO, nonexistentID));
    }

    /**
     * Given right project id & updateProjectDTO when projectService.updateProject() then update
     * project with that id
     */
    @Test
    @WithMockUser(username = "some@email.com")
    void Test008_ProjectServiceWhenReceivesValidProjectUpdateDTOAndIdShouldUpdateProject() {
        userRepository.save(owner);

        val projectToModify = projectService.createProject(projectToCreate);

        assertEquals(projectToCreate.getTitle(), projectToModify.getTitle());
        assertEquals(projectToCreate.getDescription(), projectToModify.getDescription());

        val modifiedProject =
                projectService.updateProject(projectUpdateDTO, projectToModify.getId());

        assertEquals(projectToModify.getId(), modifiedProject.getId());
        assertEquals(projectUpdateDTO.getTitle(), modifiedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), modifiedProject.getDescription());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test009_GivenASingleExistingProjectWhenDeletedTwiceThenExceptionShouldBeThrown() {
        userRepository.save(owner);

        // Given
        assertTrue(projectService.getAllProjects().isEmpty());
        Project project = projectService.createProject(projectToCreate);
        val allProjects = projectService.getAllProjects();
        assertEquals(1, allProjects.size());

        // When
        projectService.deleteProject(project.getId());

        // Then
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.deleteProject(project.getId()));
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test010_GivenValidProjectIDWhenAskedForProjectThenReturnProject() {
        userRepository.save(owner);

        Project project = projectService.createProject(projectToCreate);

        val projectToBeDisplayed = projectService.getProjectDetails(project.getId());

        assertEquals(project.getId(), projectToBeDisplayed.getId());
        assertEquals(project.getOwner().getId(), projectToBeDisplayed.getOwner().getId());
        assertEquals(projectToCreate.getTitle(), projectToBeDisplayed.getTitle());
        assertEquals(projectToCreate.getDescription(), projectToBeDisplayed.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreate.getTags()),
                projectToBeDisplayed.getTags());
        assertEquals(
                languageService.findLanguagesByNames(projectToCreate.getLanguages()),
                projectToBeDisplayed.getLanguages());
        assertEquals(projectToCreate.getLinks(), projectToBeDisplayed.getLinks());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test013_GivenExistingTagsAndAValidProjectCreateDTOWhenCreatingProjectThenItIsCreated() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "A Non Existent Project title";
        List<String> tags2 = Arrays.asList("tag1", "tag4");
        List<String> languages2 = Arrays.asList("JavaScript", "Python");

        ProjectCreateDTO projectToCreateWithRepeatedTag =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description)
                        .links(links)
                        .tags(tags2)
                        .forumTags(forumTags)
                        .languages(languages2)
                        .build();

        val project = projectService.createProject(projectToCreateWithRepeatedTag);

        assertEquals(projectToCreateWithRepeatedTag.getTitle(), project.getTitle());
        assertEquals(projectToCreateWithRepeatedTag.getDescription(), project.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreateWithRepeatedTag.getTags()),
                project.getTags());
        assertEquals(projectToCreateWithRepeatedTag.getLinks(), project.getLinks());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test014_ProjectServiceWhenReceivesValidProjectUpdateDTOAndIdShouldUpdateProjectAndDeleteUnusedTags() {
        userRepository.save(owner);

        Project createdProject = projectService.createProject(projectToCreate);

        List<Tag> tags = tagService.getAllTags();
        assertEquals(2, tags.size());
        assertEquals("tag1", createdProject.getTags().get(0).getName());
        assertEquals("tag2", createdProject.getTags().get(1).getName());

        val updatedProject = projectService.updateProject(projectUpdateDTO, createdProject.getId());

        assertEquals(createdProject.getId(), updatedProject.getId());
        assertEquals(projectUpdateDTO.getTitle(), updatedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), updatedProject.getDescription());
        assertEquals(tagService.findTagsByNames(tagsUpdate), updatedProject.getTags());
        assertEquals(projectUpdateDTO.getLinks(), updatedProject.getLinks());

        List<Tag> updatedTags = tagService.getAllTags();
        assertEquals(2, updatedTags.size());
        assertEquals("tag1", updatedProject.getTags().get(0).getName());
        assertEquals("tag4", updatedProject.getTags().get(1).getName());
    }

    @Test
    void Test015_ProjectServiceShouldReturnListWithAllLanguageValidNames() {
        String validLanguageNames =
                "Java, C, C++, C#, Python, Visual Basic .NET, PHP, JavaScript, TypeScript, Delphi/Object Pascal, Swift, Perl, Ruby, Assembly language, R, Visual Basic, Objective-C, Go, MATLAB, PL/SQL, Scratch, SAS, D, Dart, ABAP, COBOL, Ada, Fortran, Transact-SQL, Lua, Scala, Logo, F#, Lisp, LabVIEW, Prolog, Haskell, Scheme, Groovy, RPG (OS/400), Apex, Erlang, MQL4, Rust, Bash, Ladder Logic, Q, Julia, Alice, VHDL, Awk, (Visual) FoxPro, ABC, ActionScript, APL, AutoLISP, bc, BlitzMax, Bourne shell, C shell, CFML, cg, CL (OS/400), Clipper, Clojure, Common Lisp, Crystal, Eiffel, Elixir, Elm, Emacs Lisp, Forth, Hack, Icon, IDL, Inform, Io, J, Korn shell, Kotlin, Maple, ML, NATURAL, NXT-G, OCaml, OpenCL, OpenEdge ABL, Oz, PL/I, PowerShell, REXX, Ring, S, Smalltalk, SPARK, SPSS, Standard ML, Stata, Tcl, VBScript, Verilog";
        List<String> validLanguageList =
                new ArrayList<>(Arrays.asList(validLanguageNames.split(", ")));

        assertEquals(validLanguageList, projectService.getValidLanguageNames());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test014_ProjectServiceWhenReceivesValidProjectUpdateDTOAndIdShouldUpdateProjectAndDeleteUnusedLanguages() {
        userRepository.save(owner);

        Project createdProject = projectService.createProject(projectToCreate);

        List<Language> languages = languageService.getAllLanguages();
        assertEquals(2, languages.size());
        assertEquals("Java", createdProject.getLanguages().get(0).getName());
        assertEquals("C", createdProject.getLanguages().get(1).getName());

        val updatedProject = projectService.updateProject(projectUpdateDTO, createdProject.getId());

        assertEquals(createdProject.getId(), updatedProject.getId());
        assertEquals(projectUpdateDTO.getTitle(), updatedProject.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), updatedProject.getDescription());
        assertEquals(tagService.findTagsByNames(tagsUpdate), updatedProject.getTags());
        assertEquals(
                languageService.findLanguagesByNames(languagesUpdate),
                updatedProject.getLanguages());
        assertEquals(projectUpdateDTO.getLinks(), updatedProject.getLinks());

        List<Language> updatedLanguages = languageService.getAllLanguages();
        assertEquals(2, updatedLanguages.size());
        assertEquals("Java", updatedProject.getLanguages().get(0).getName());
        assertEquals("Ruby", updatedProject.getLanguages().get(1).getName());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void
            Test016_GivenExistingLanguagesAndAValidProjectCreateDTOWhenCreatingProjectThenItIsCreated() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "A Non Existent Project title";
        List<String> tags2 = Arrays.asList("tag5", "tag6");
        List<String> languages2 = Arrays.asList("Java", "Python");

        ProjectCreateDTO projectToCreateWithRepeatedTag =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description)
                        .links(links)
                        .tags(tags2)
                        .forumTags(forumTags)
                        .languages(languages2)
                        .build();

        val project = projectService.createProject(projectToCreateWithRepeatedTag);

        assertEquals(projectToCreateWithRepeatedTag.getTitle(), project.getTitle());
        assertEquals(projectToCreateWithRepeatedTag.getDescription(), project.getDescription());
        assertEquals(
                tagService.findTagsByNames(projectToCreateWithRepeatedTag.getTags()),
                project.getTags());
        assertEquals(
                languageService.findLanguagesByNames(projectToCreateWithRepeatedTag.getLanguages()),
                project.getLanguages());
        assertEquals(projectToCreateWithRepeatedTag.getLinks(), project.getLinks());
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test017_SearchProjectsByLangsCaseInsensitive() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "A Non Existent Project title";
        List<String> tags2 = Arrays.asList("tag5", "tag6");
        List<String> languages2 = Arrays.asList("Java", "Python");

        ProjectCreateDTO projectToCreateWithRepeatedTag =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description)
                        .links(links)
                        .tags(tags2)
                        .forumTags(forumTags)
                        .languages(languages2)
                        .build();
        ProjectSearchDTO projectSeached =
                ProjectSearchDTO.builder().tags(tags2).languages(Arrays.asList("pyTHoN")).build();
        val project = projectService.createProject(projectToCreateWithRepeatedTag);

        assertEquals(projectToCreateWithRepeatedTag.getTitle(), project.getTitle());
        assertEquals(projectToCreateWithRepeatedTag.getDescription(), project.getDescription());
        assertEquals(
                projectService.searchProjectsByFilter(projectSeached).get(0).getTitle(), title2);
    }

    @Test
    @WithMockUser(username = "some@email.com")
    void Test018_SearchProjectsByTagsCaseInsensitive() {
        userRepository.save(owner);

        projectService.createProject(projectToCreate);

        String title2 = "A Non Existent Project title";
        List<String> tags2 = Arrays.asList("tag5", "tag6");
        List<String> languages2 = Arrays.asList("Java", "Python");

        ProjectCreateDTO projectToCreateWithRepeatedTag =
                ProjectCreateDTO.builder()
                        .title(title2)
                        .description(description)
                        .links(links)
                        .tags(tags2)
                        .forumTags(forumTags)
                        .languages(languages2)
                        .build();
        ProjectCreateDTO projectToCreate2 =
                ProjectCreateDTO.builder()
                        .title("ProjectTitle")
                        .description(description)
                        .links(links)
                        .tags(Arrays.asList("tag7", "tag8"))
                        .forumTags(forumTagsUpdate)
                        .languages(languages2)
                        .build();

        projectService.createProject(projectToCreateWithRepeatedTag);
        projectService.createProject(projectToCreate2);
        ProjectSearchDTO projectSeached = ProjectSearchDTO.builder().title("ProjectT").build();

        assertEquals(projectService.searchProjectsByFilter(projectSeached).size(), 1);
        assertEquals(
                projectService.searchProjectsByFilter(projectSeached).get(0).getTitle(), title2);
    }
}
