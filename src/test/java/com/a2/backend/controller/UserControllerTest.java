package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.entity.User;
import com.a2.backend.model.UserCreateDTO;
import com.a2.backend.model.UserUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired TestRestTemplate restTemplate;

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    private final String baseUrl = "/user";
    private final String confirmationUrl = "/confirm";

    private final String nickname = "nickname";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";
    private final String confirmationToken = "token001";

    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .confirmationToken(confirmationToken)
                    .build();

    @Test
    void
            Test001_GivenAValidUserCreateDTOWhenRequestingPostThenReturnStatusCreatedAndPersistedUserAreReturned() {

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

        userCreateDTO.setNickname("not a valid nickname as it is way too long");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Nickname must be between 3 and 24 characters", getResponse.getBody());
    }

    @Test
    void
            Test003_GivenAUserCreateDTOWithInvalidEmailWhenCreatingUserThenBadStatusResponseIsReturned() {

        userCreateDTO.setEmail("this is not a real email");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Email must be valid", getResponse.getBody());
    }

    @Test
    void
            Test004_GivenAUserCreateDTOWithInvalidPasswordWhenCreatingUserThenBadStatusResponseIsReturned() {

        userCreateDTO.setPassword("short");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("Password must be between 8 and 32 characters", getResponse.getBody());
    }

    @Test
    void
            Test005_GivenAUserCreateDTOWithNoBiographyWhenCreatingUserThenStatusIsCreatedAndBiographyIsNull() {

        userCreateDTO.setBiography(null);

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
                "There is an existing user with the nickname " + nickname,
                anotherGetResponse.getBody());
    }

    @Test
    void
            Test007_GivenAUserCreateDTOWithExistingEmailWhenCreatingUserThenExceptionIsHandledAndBadRequestIsReturned() {

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
    @WithMockUser(username = "some@gmail.com")
    void Test008_GivenAnExistingUserWhenDeletedThenANewUserWithSameNicknameAndEmailCanBeCreated()
            throws Exception {

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        assertNotNull(postResponse.getBody());

        mvc.perform(MockMvcRequestBuilders.delete(baseUrl).accept(MediaType.ALL))
                .andExpect(status().isOk());

        val anotherPostResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, anotherPostResponse.getStatusCode());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test009_GivenANonExistentEmailWhenDeletingUserThenExceptionIsHandledAndBadRequestIsReturned()
                    throws Exception {
        val deleteResponse =
                mvc.perform(MockMvcRequestBuilders.delete(baseUrl).accept(MediaType.ALL))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse();

        assertEquals(
                "No user found for email: some@gmail.com", deleteResponse.getContentAsString());
    }

    @Test
    void Test010_GivenAValidTokenAndUserWhenConfirmingUserThenStatusOKisReturned() {

        String validConfirmationToken = "token001";

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val userToActivate = postResponse.getBody();
        assertNotNull(userToActivate);

        val getResponse =
                restTemplate.exchange(
                        String.format(
                                "%s/%s/%s/%s",
                                baseUrl,
                                confirmationUrl,
                                userToActivate.getId(),
                                validConfirmationToken),
                        HttpMethod.GET,
                        null,
                        User.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        val activatedUser = getResponse.getBody();
        assertNotNull(activatedUser);
        assertTrue(activatedUser.isActive());

        System.out.println("This is a password: " + userToActivate.getPassword());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test011_GivenAValidUserUpdateDTOAndAnExistingUserWhenUpdatingThenAUserWithPreviousNicknameCanBeCreated()
                    throws Exception {

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        UserUpdateDTO userUpdateDTO =
                UserUpdateDTO.builder()
                        .nickname("updated nickname")
                        .biography("updated biography")
                        .password("updated password")
                        .build();

        mvc.perform(
                        MockMvcRequestBuilders.put(baseUrl)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateDTO))
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        userCreateDTO.setEmail("new@email.com");
        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(userCreateDTO);

        val updatedPostResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, User.class);
        assertEquals(HttpStatus.CREATED, updatedPostResponse.getStatusCode());
        assertNotNull(updatedPostResponse.getBody());

        assertEquals(nickname, updatedPostResponse.getBody().getNickname());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test012_GivenAUserUpdateDTOWithInvalidNicknameWhenUpdatingUserThenBadStatusResponseIsReturned()
                    throws Exception {

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        String invalidNickname = "not a valid nickname as it is way too long";
        UserUpdateDTO userUpdateDTO =
                UserUpdateDTO.builder().nickname(invalidNickname).password(password).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Nickname must be between 3 and 24 characters", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test013_GivenAUserUpdateDTOWithInvalidPasswordWhenUpdatingUserThenBadStatusResponseIsReturned()
                    throws Exception {

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        String invalidPassword = "short";
        UserUpdateDTO userUpdateDTO =
                UserUpdateDTO.builder().nickname(nickname).password(invalidPassword).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("Password must be between 8 and 32 characters", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test014_GivenAUserUpdateDTOWithExistingNicknameWhenUpdatingUserThenExceptionIsHandledAndBadRequestIsReturned()
                    throws Exception {

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        String anotherNickname = "nickname1";
        String anotherEmail = "another@gmail.com";
        String anotherBiography = "another bio";
        String anotherPassword = "anotherPassword";

        UserCreateDTO UserCreateDTO1 =
                UserCreateDTO.builder()
                        .nickname(anotherNickname)
                        .email(anotherEmail)
                        .biography(anotherBiography)
                        .password(anotherPassword)
                        .build();

        HttpEntity<UserCreateDTO> anotherRequest = new HttpEntity<>(UserCreateDTO1);

        val anotherPostResponse =
                restTemplate.exchange(baseUrl, HttpMethod.POST, anotherRequest, User.class);
        assertEquals(HttpStatus.CREATED, anotherPostResponse.getStatusCode());

        UserUpdateDTO userUpdateDTO =
                UserUpdateDTO.builder()
                        .nickname(anotherNickname)
                        .biography(biography)
                        .password(password)
                        .build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals(
                "There is an existing user with the nickname " + anotherNickname, errorMessage);
    }
}
