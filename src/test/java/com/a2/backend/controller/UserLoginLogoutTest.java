package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserLoginLogoutTest {

    @Autowired TestRestTemplate restTemplate;
    private final String baseUrl = "/user";
    private final String confirmationUrl = "/confirm";

    private final String nickname = "nickname";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";
    private final String confirmationToken = "token001";

    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void Test001_GivenAnExistingUserLoginShouldSucceed() {
        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .confirmationToken(confirmationToken)
                        .build();
        String validConfirmationToken = "token001";

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val userToActivate = postResponse.getBody();

        val getResponse =
                restTemplate.exchange(
                        String.format(
                                "%s/%s/%s/%s",
                                baseUrl,
                                confirmationUrl,
                                validConfirmationToken,
                                userToActivate.getId()),
                        HttpMethod.GET,
                        null,
                        User.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        val activatedUser = getResponse.getBody();
        assertTrue(activatedUser.isActive());

        User loginUser = User.builder().email(email).password(password).build();

        HttpEntity<User> loginRequest = new HttpEntity<>(loginUser);
        val loginResponse =
                restTemplate.exchange("/login", HttpMethod.POST, loginRequest, User.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    }

    @Test
    void Test002_GivenNonExistingUserLoginShouldFail() {
        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email + "l")
                        .biography(biography)
                        .password(password)
                        .confirmationToken(confirmationToken)
                        .build();
        String validConfirmationToken = "token001";

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        User loginUser = User.builder().email(email).password(password).build();

        HttpEntity<User> loginRequest = new HttpEntity<>(loginUser);
        val loginResponse =
                restTemplate.exchange("/login", HttpMethod.POST, loginRequest, User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }

    @Test
    void Test003_GivenNonActiveUserLoginShouldFail() {
        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .confirmationToken(confirmationToken)
                        .build();
        String validConfirmationToken = "token001";

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        User loginUser = User.builder().email(email).password(password).build();

        HttpEntity<User> loginRequest = new HttpEntity<>(loginUser);
        val loginResponse =
                restTemplate.exchange("/login", HttpMethod.POST, loginRequest, User.class);
        assertEquals(HttpStatus.UNAUTHORIZED, loginResponse.getStatusCode());
    }
}
