package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.entity.Project;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
import java.util.ArrayList;
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test003_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionShouldReturnStatusBadRequest() {

        String title = "Project title";
        String description = "Short";
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void
            Test004_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionAndTitleShouldReturnStatusBadRequest() {
        String title = "a";
        String description = "Short";
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
    }

    @Test
    void Test009_ProjectControllerWhenReceivesValidProjectUpdateDTOShouldReturnHttpOkTest() {
        String title = "Project title";
        String description = "Testing exception for existing title";
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
        String[] links = {"link1", "link2"};
        String[] tags = {"tag1", "tag2"};
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
}
