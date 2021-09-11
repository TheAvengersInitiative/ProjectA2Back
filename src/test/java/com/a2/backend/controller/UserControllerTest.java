package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;

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
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserControllerTest {

    @Autowired TestRestTemplate restTemplate;

    private final String baseUrl = "/user";
    private final String confirmationUrl = "/confirm";

    private final String nickname = "nickname";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";
    private final String confirmationToken = "token001";

    @Test
    void
            Test001_GivenAValidUserCreateDTOWhenRequestingPostThenReturnStatusCreatedAndPersistedUserAreReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        val persistedUser = getResponse.getBody();

        assertNotNull(persistedUser);
        assertEquals(nickname, persistedUser.getNickname());
        assertEquals(email, persistedUser.getEmail());
        assertEquals(biography, persistedUser.getBiography());
        assertNotEquals(password, persistedUser.getPassword());
        assertFalse(persistedUser.isActive());
    }

    @Test
    void
            Test002_GivenAUserCreateDTOWithInvalidNicknameWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidNickname = "not a valid nickname as it is way too long";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(invalidNickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Nickname must be between 3 and 24 characters", getResponse.getBody());
    }

    @Test
    void
            Test003_GivenAUserCreateDTOWithInvalidEmailWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidEmail = "this is not a real email";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(invalidEmail)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Email must be valid", getResponse.getBody());
    }

    @Test
    void
            Test004_GivenAUserCreateDTOWithInvalidPasswordWhenCreatingUserThenBadStatusResponseIsReturned() {
        String invalidPassword = "short";

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(invalidPassword)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Password must be between 8 and 32 characters", getResponse.getBody());
    }

    @Test
    void
            Test005_GivenAUserCreateDTOWithNoBiographyWhenCreatingUserThenStatusIsCreatedAndBiographyIsNull() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder().nickname(nickname).email(email).password(password).build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        val persistedUser = getResponse.getBody();
        assertNotNull(persistedUser);
        assertNull(persistedUser.getBiography());
    }

    @Test
    void
            Test006_GivenAUserCreateDTOWithExistingNicknameWhenCreatingUserThenExceptionIsHandledAndBadRequestIsReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        String anotherEmail = "another@gmail.com";
        String anotherBiography = "another bio";
        String anotherPassword = "anotherPassword";

        UserCreateDTO sameNicknameUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(anotherEmail)
                        .biography(anotherBiography)
                        .password(anotherPassword)
                        .build();

        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(sameNicknameUserCreateDTO);

        val anotherGetResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, anotherGetResponse.getStatusCode());
        assertEquals(
                "There is an existing user the nickname " + nickname, anotherGetResponse.getBody());
    }

    @Test
    void
            Test007_GivenAUserCreateDTOWithExistingEmailWhenCreatingUserThenExceptionIsHandledAndBadRequestIsReturned() {

        UserCreateDTO userCreateDTO =
                UserCreateDTO.builder()
                        .nickname(nickname)
                        .email(email)
                        .biography(biography)
                        .password(password)
                        .build();

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        String anotherNickname = "anotherNickname";
        String anotherBiography = "another bio";
        String anotherPassword = "anotherPassword";

        UserCreateDTO sameNicknameUserCreateDTO =
                UserCreateDTO.builder()
                        .nickname(anotherNickname)
                        .email(email)
                        .biography(anotherBiography)
                        .password(anotherPassword)
                        .build();

        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(sameNicknameUserCreateDTO);

        val anotherGetResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, anotherGetResponse.getStatusCode());
        assertEquals(
                "There is an existing user with the email " + email, anotherGetResponse.getBody());
    }

    @Test
    void Test008_GivenAValidTokenAndUserWhenConfirmingUserThenStatusOKisReturned() {
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
    }
}
