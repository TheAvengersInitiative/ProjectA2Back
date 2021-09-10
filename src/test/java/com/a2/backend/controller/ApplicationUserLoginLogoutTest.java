package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.a2.backend.entity.ApplicationUser;
import com.a2.backend.model.ApplicationUserCreateDTO;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ApplicationUserLoginLogoutTest {

    @Autowired TestRestTemplate restTemplate;
    private final String baseUrl = "/user";
    private final String nickname = "estaTareaEsUnQuilombo";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";

    @Test
    void Test001_GivenAnExistingUserLoginIsSuccesfull() {

        ApplicationUserCreateDTO applicationUserCreateDTO =
                ApplicationUserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();
        ApplicationUser user = ApplicationUser.builder().email(email).password(password).build();
        HttpEntity<ApplicationUserCreateDTO> request = new HttpEntity<>(applicationUserCreateDTO);

        val getResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, request, ApplicationUser.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());
        System.out.println(getResponse);
        HttpEntity<ApplicationUser> loginRequest = new HttpEntity<>(user);
        val loginResponse =
                restTemplate.exchange("/login", HttpMethod.POST, loginRequest, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Authorization", loginResponse.getHeaders().get("token").get(0));
        HttpEntity<String> secureRequest = new HttpEntity<>(null, headers);
        val securedResponse =
                restTemplate.exchange("/api/secure", HttpMethod.GET, secureRequest, String.class);
        assertEquals(
                securedResponse.getBody(),
                "If your are reading this you reached a secure endpoint");
    }

    @Test
    void Test002_GivenANonExistingUserLoginShouldFail() {

        ApplicationUserCreateDTO applicationUserCreateDTO =
                ApplicationUserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();
        ApplicationUser user =
                ApplicationUser.builder()
                        .nickname("estatareanomelades")
                        .email("jjjjjjjjjjjjj@gmail.com")
                        .biography(biography)
                        .password(password)
                        .build();
        HttpEntity<ApplicationUserCreateDTO> request = new HttpEntity<>(applicationUserCreateDTO);

        val getResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, request, ApplicationUser.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        HttpEntity<ApplicationUser> loginRequest = new HttpEntity<>(user);
        val loginResponse =
                restTemplate.exchange("/login", HttpMethod.POST, loginRequest, String.class);
        assertEquals(loginResponse.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}
