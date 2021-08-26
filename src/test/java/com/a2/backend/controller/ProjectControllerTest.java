package com.a2.backend.controller;

import com.a2.backend.entity.Project;
import com.a2.backend.exception.ProjectWithThatTitleExistsException;
import com.a2.backend.model.ProjectCreateDTO;
import com.a2.backend.model.ProjectUpdateDTO;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProjectControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl = "/project";

    @Test
    void Test001_ProjectControllerWhenReceivesValidCreateProjectDTOShouldReturnStatusCreated() {
        String title = "Project title";
        String description = "Testing exception for existing title";
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());
    }

    @Test
    void Test002_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidTitleShouldReturnStatusBadRequest() {
        String title = "a";
        String description = "Testing exception for existing title";
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test003_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionShouldReturnStatusBadRequest() {
        String title = "Project title";
        String description = "Short";
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test004_ProjectControllerWhenReceiveCreateProjectDTOWithInvalidDescriptionAndTitleShouldReturnStatusBadRequest() {
        String title = "a";
        String description = "Short";
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
    }

    @Test
    void Test005_GettingAllProjectsShouldReturnHttpOkTest() {
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    }

    @Test
    void Test006_GivenASingleExistingProjectWhenDeletedThenThereAreNoExistingProjects() {

        String title = "Project title";
        String description = "Testing exception for existing title";
        String owner = "Owner´s name";

        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, null, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assertTrue(projects.length == 1);

        String deleteResponse = restTemplate.exchange(String.format("%s/%s", baseUrl, projects[0].getId()), HttpMethod.DELETE, null, String.class).getBody();

        assertTrue(deleteResponse.equals(projects[0].getId()));
    }


    @Test
    void Test_001_updateProjectController() {
        String title = "Project title";
        String description = "Testing exception for existing title";
        String owner = "Owner´s name";

        String titleforUpdate = "New Project Title";
        String descriptionforUpdate = "New Project description";


        ProjectCreateDTO projectToCreate = ProjectCreateDTO.builder()
                .title(title)
                .description(description)
                .owner(owner)
                .build();

        ProjectUpdateDTO projectUpdateDTO = ProjectUpdateDTO.builder()
                .title(titleforUpdate)
                .description(descriptionforUpdate)
                .build();

        HttpEntity<ProjectCreateDTO> request = new HttpEntity<>(projectToCreate);
        HttpEntity<ProjectUpdateDTO> requestUpdate = new HttpEntity<>(projectUpdateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, Project.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, request, Project[].class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Project[] projects = getResponse.getBody();

        assertTrue(projects.length == 1);

        val updatedResponse = restTemplate.exchange(String.format("%s/%s", baseUrl, projects[0].getId()), HttpMethod.PUT, requestUpdate, Project.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());

        Project project = updatedResponse.getBody();

        assertEquals(projectUpdateDTO.getTitle(), project.getTitle());
        assertEquals(projectUpdateDTO.getDescription(), project.getDescription());

//        Project updatedResponse = restTemplate.exchange(String.format("%s/%s",baseUrl,projects[0].getId()), HttpMethod.PUT, requestUpdate, Project.class).getBody();
//
//        assertTrue(projectUpdateDTO.getTitle().equals( updatedResponse.getTitle()));
    }

}