package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
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
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectControllerTest {

    @Autowired private TestRestTemplate restTemplate;

    private final String baseUrl = "/project";

    @Test
    void Test001_ProjectControllerWhenReceivesValidCreateProjectDTOShouldReturnStatusCreated() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());
    }

    @Test
    void
            Test002_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidTitleShouldReturnStatusBadRequest() {

        String title = "a";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Title must be between 3 and 100 characters", getResponse.getBody());
    }

    @Test
    void
            Test003_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionShouldReturnStatusBadRequest() {

        String title = "Project title";
        String description = "Short";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Description must be between 10 and 500 characters", getResponse.getBody());
    }

    @Test
    void
            Test004_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionAndTitleShouldReturnStatusBadRequest() {
        String title = "a";
        String description = "Short";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test005_GivenNoExistingProjectsWhenGettingAllProjectsThenEmptyResponseIsReturned() {
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(0, getResponse.getBody().length);
    }

    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenProjectIdIsReturned() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assertEquals(1, projects.length);

        val deleteResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, projects[0].getId()),
                        HttpMethod.DELETE,
                        null,
                        UUID.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertEquals(projects[0].getId(), deleteResponse.getBody());
    }

    @Test
    void Test007_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assertEquals(1, projects.length);

        val deleteResponse =
                restTemplate
                        .exchange(
                                String.format("%s/%s", baseUrl, projects[0].getId()),
                                HttpMethod.DELETE,
                                null,
                                UUID.class)
                        .getBody();
        assertEquals(deleteResponse, projects[0].getId());

        val getResponse1 = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse1.getStatusCode());
        Project[] projects1 = getResponse1.getBody();

        assertEquals(0, projects1.length);
    }

    @Test
    void Test008_GivenASingleExistingProjectWhenDeletedTwiceErrorShouldBeThrown() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assertEquals(1, projects.length);

        val deleteResponse =
                restTemplate
                        .exchange(
                                String.format("%s/%s", baseUrl, projects[0].getId()),
                                HttpMethod.DELETE,
                                null,
                                UUID.class)
                        .getBody();
        assertEquals(deleteResponse, projects[0].getId());

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
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        String titleforUpdate = "New Project Title";
        String descriptionforUpdate = "New Project description";
        List<String> linksForUpdate = new ArrayList<>();
        linksForUpdate.add("link1");
        linksForUpdate.add("link2");
        List<String> tagsForUpdate = new ArrayList<>();
        tagsForUpdate.add("tag1");
        tagsForUpdate.add("tag2");

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        ProjectUpdateDTO projectUpdateDTO =
                ProjectUpdateDTO.builder()
                        .title(titleforUpdate)
                        .description(descriptionforUpdate)
                        .links(linksForUpdate)
                        .tags(tagsForUpdate)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);
        HttpEntity<ProjectUpdateDTO> requestUpdate = new HttpEntity<>(projectUpdateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val updatedResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, postResponse.getBody().getId()),
                        HttpMethod.PUT,
                        requestUpdate,
                        Project.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        Project project = updatedResponse.getBody();

        assertEquals(projectUpdateDTO.getTitle(), project.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), project.getDescription());
    }

    /** Given valid ID Should return */
    @Test
    void Test010_ProjectControllerWhenReceivesValidIdShouldReturnHttpOkTest() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getProjectDetailsResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, postResponse.getBody().getId()),
                        HttpMethod.GET,
                        null,
                        Project.class);
        assertEquals(HttpStatus.OK, getProjectDetailsResponse.getStatusCode());

        Project project = getProjectDetailsResponse.getBody();

        assertEquals(projectToCreate.getOwner(), project.getOwner());
        assertEquals(projectToCreate.getTitle(), project.getTitle());
        assertEquals(projectToCreate.getDescription(), project.getDescription());
    }

    @Test
    void Test011_GivenAnExistingProjectWhenGettingAllProjectsThenItIsReturned() {
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Project[] projects = getResponse.getBody();
        assertEquals(1, projects.length);
        assertEquals(title, projects[0].getTitle());
        assertEquals(owner, projects[0].getOwner());
    }

    @Test

    void
            Test012_ProjectControllerWhenReceiveCreateProjectDTOWithTagShorterThanOneCharacterShouldReturnStatusBadRequest() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test013_ProjectControllerWhenReceiveCreateProjectDTOWithTagLargerThanTwentyfourCharactersShouldReturnStatusBadRequest() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2");
        List<String> tags = Arrays.asList("This is not a valid tag for a project", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test014_ProjectControllerWhenReceiveCreateProjectDTOWithNoLinksShouldReturnStatusBadRequest() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList();
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();


        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test015_ProjectControllerWhenReceiveCreateProjectDTOWithMoreThanFiveLinksShouldReturnStatusBadRequest() {
        String title = "Project title";
        String description = "Testing exception for existing title";
        List<String> links = Arrays.asList("link1", "link2", "link3", "link4", "link5", "link6");
        List<String> tags = Arrays.asList("tag1", "tag2");
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();
        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test016_ProjectControllerSuccesfulOrderedSearch() {
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
        String owner = "Owner´s name";
        String secondOwner = "Owner´s name2";

        ProjectCreateDTO firstProjectToCreate =
                ProjectCreateDTO.builder()
                        .title(title)
                        .description(description)
                        .links(links)
                        .tags(tags)
                        .owner(owner)
                        .build();
        ProjectCreateDTO secondProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Not Start Project")
                        .description(description)
                        .links(secondLinks)
                        .tags(secondTags)
                        .owner(owner)
                        .build();
        ProjectCreateDTO thirdProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project2 Title")
                        .description(description)
                        .links(thirdLinks)
                        .tags(thirdTags)
                        .owner(owner)
                        .build();
        ProjectCreateDTO fourthProjectToCreate =
                ProjectCreateDTO.builder()
                        .title("Project3 Title")
                        .description(description)
                        .links(fourthLinks)
                        .tags(fourthTags)
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
                        "/project/title/Project", HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Project[] projects = getResponse.getBody();
        System.out.println(projects);
        assertEquals(4, projects.length);
        assertEquals(title, projects[0].getTitle());

        assertEquals("Project2 Title", projects[1].getTitle());
        assertEquals("Project3 Title", projects[2].getTitle());
        assertEquals("Not Start Project", projects[3].getTitle());
    }
}
