package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.AbstractTest;
import com.a2.backend.entity.User;
import com.a2.backend.model.*;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
class UserControllerTest extends AbstractTest {

    @Autowired TestRestTemplate restTemplate;

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    private final String baseUrl = "/user";
    private final String confirmationUrl = "confirm";
    private final String recoverUrl = "recover";
    private final String requestUrl = "request";

    private final String nickname = "nickname";
    private final String email = "some@gmail.com";
    private final String biography = "bio";
    private final String password = "password";
    private final String confirmationToken = "token001";
    private final String recoveryToken = "RecoveryToken";

    UserCreateDTO userCreateDTO =
            UserCreateDTO.builder()
                    .nickname(nickname)
                    .email(email)
                    .biography(biography)
                    .password(password)
                    .build();

    List<String> tags = List.of("tag1", "tag2");
    List<String> languages = List.of("Java", "C");

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

        userCreateDTO.setNickname("notAValidNicknameAsItIsWayTooLong");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals(
                "nickname: Nickname must be between 3 and 24 characters\n", getResponse.getBody());
    }

    @Test
    void
            Test003_GivenAUserCreateDTOWithInvalidEmailWhenCreatingUserThenBadStatusResponseIsReturned() {

        userCreateDTO.setEmail("this is not a real email");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("email: Email must be valid\n", getResponse.getBody());
    }

    @Test
    void
            Test004_GivenAUserCreateDTOWithInvalidPasswordWhenCreatingUserThenBadStatusResponseIsReturned() {

        userCreateDTO.setPassword("short");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals(
                "password: Password must be between 8 and 32 characters\n", getResponse.getBody());
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
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken(confirmationToken)
                        .id(postResponse.getBody().getId())
                        .build();

        val confirmationToken =
                userRepository
                        .findById(postResponse.getBody().getId())
                        .get()
                        .getConfirmationToken();
        confirmationTokenDTO.setConfirmationToken(confirmationToken);

        HttpEntity<ConfirmationTokenDTO> updatedRequest = new HttpEntity<>(confirmationTokenDTO);

        val postNewResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, confirmationUrl),
                        HttpMethod.POST,
                        updatedRequest,
                        User.class);
        assertEquals(HttpStatus.OK, postNewResponse.getStatusCode());

        val activatedUser = postNewResponse.getBody();

        assertEquals(postResponse.getBody().getPassword(), activatedUser.getPassword());
        assertEquals(postResponse.getBody().getId(), activatedUser.getId());
        assertTrue(activatedUser.isActive());
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
                        .nickname("updatedNickname")
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

        String invalidNickname = "notAValidNicknameAsItIsWayTooLong";
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

        assertEquals("nickname: Nickname must be between 3 and 24 characters\n", errorMessage);
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

        assertEquals("password: Password must be between 8 and 32 characters\n", errorMessage);
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

    @Test
    void Test015_GivenAValidUserWhenWantToRecoverPasswordThenReturnStatusOk() {

        HttpEntity<UserCreateDTO> userRequest = new HttpEntity<>(userCreateDTO);
        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, userRequest, User.class);
        assertEquals(HttpStatus.CREATED, getResponse.getStatusCode());

        val recoverPasswordToken =
                userRepository
                        .findById(getResponse.getBody().getId())
                        .get()
                        .getPasswordRecoveryToken();

        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken(confirmationToken)
                        .id(getResponse.getBody().getId())
                        .build();

        val confirmationToken =
                userRepository.findById(getResponse.getBody().getId()).get().getConfirmationToken();
        confirmationTokenDTO.setConfirmationToken(confirmationToken);

        HttpEntity<ConfirmationTokenDTO> updatedRequest = new HttpEntity<>(confirmationTokenDTO);

        val getActivatedResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, confirmationUrl),
                        HttpMethod.POST,
                        updatedRequest,
                        User.class);
        assertEquals(HttpStatus.OK, getActivatedResponse.getStatusCode());

        PasswordRecoveryDTO passwordRecoveryDTO =
                PasswordRecoveryDTO.builder()
                        .id(getActivatedResponse.getBody().getId())
                        .passwordRecoveryToken(recoveryToken)
                        .newPassword("NewPassword001")
                        .build();

        passwordRecoveryDTO.setPasswordRecoveryToken(recoverPasswordToken);
        HttpEntity<PasswordRecoveryDTO> passwordRecoveryRequest =
                new HttpEntity<>(passwordRecoveryDTO);

        val postResponse =
                restTemplate.exchange(
                        String.format("%s/%s/%s", baseUrl, recoverUrl, requestUrl),
                        HttpMethod.POST,
                        passwordRecoveryRequest,
                        User.class);
        assertEquals(HttpStatus.OK, postResponse.getStatusCode());

        getActivatedResponse
                .getBody()
                .setPasswordRecoveryToken(passwordRecoveryDTO.getPasswordRecoveryToken());
        val activatedUser = getActivatedResponse.getBody();

        assertEquals(getActivatedResponse.getStatusCode(), HttpStatus.OK);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test016_GivenAValidPreferencesUpdateDTOAndAnExistingUserWhenUpdatingPreferencesThenTheyAreUpdated()
                    throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tags).languages(languages).build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(String.format("%s/preferences", baseUrl))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        preferencesUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        User userWithPreferences = objectMapper.readValue(contentAsString, User.class);

        assertNotNull(userWithPreferences.getId());
        assertEquals(languages, userWithPreferences.getPreferredLanguages());
        assertEquals(tags, userWithPreferences.getPreferredTags());
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test017_GivenAPreferencesUpdateDTOWithTooManyLanguagesWhenUpdatingPreferencesThenBadRequestIsReturned()
                    throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        List<String> tooManyLanguages = List.of("Java", "C", "C++", "Rust");

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tags).languages(tooManyLanguages).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(String.format("%s/preferences", baseUrl))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        preferencesUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("languages: Maximum number of languages is 3\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test018_GivenAPreferencesUpdateDTOWithTooManyTagsWhenUpdatingPreferencesThenBadRequestIsReturned()
                    throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        List<String> tooManyTags = List.of("tag1", "tag2", "tag3", "tag4", "tag5");

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tooManyTags).languages(languages).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(String.format("%s/preferences", baseUrl))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        preferencesUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("tags: Maximum number of tags is 4\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test019_GivenAPreferencesUpdateDTOWithRepeatedLanguagesWhenUpdatingPreferencesThenBadRequestIsReturned()
                    throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        List<String> repeatedLanguages = List.of("Java", "Java", "C");

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(tags).languages(repeatedLanguages).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(String.format("%s/preferences", baseUrl))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        preferencesUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("languages: Languages must be unique\n", errorMessage);
    }

    @Test
    @WithMockUser(username = "some@gmail.com")
    void
            Test020_GivenAPreferencesUpdateDTOWithRepeatedTagsWhenUpdatingPreferencesThenBadRequestIsReturned()
                    throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        List<String> repeatedTags = List.of("tag1", "tag2", "tag2", "tag3");

        PreferencesUpdateDTO preferencesUpdateDTO =
                PreferencesUpdateDTO.builder().tags(repeatedTags).languages(languages).build();

        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.put(String.format("%s/preferences", baseUrl))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        preferencesUpdateDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("tags: Tag names must be unique\n", errorMessage);
    }

    @Test
    void
            Test021_GivenAUserCreateDTOWithNicknameContainingSpacesWhenCreatingUserThenBadStatusResponseIsReturned() {

        userCreateDTO.setNickname("new user");

        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val getResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        assertEquals("nickname: Invalid pattern for field\n", getResponse.getBody());
    }

    @Test
    @WithMockUser("some@gmail.com")
    void Test022_GivenALoggedUserWhenGettingLoggedUserThenItIsReturned() throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.ALL))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        User loggedUser = objectMapper.readValue(contentAsString, User.class);

        assertEquals(nickname, loggedUser.getNickname());
        assertEquals(email, loggedUser.getEmail());
        assertEquals(biography, loggedUser.getBiography());
        assertNotEquals(password, loggedUser.getPassword());
    }

    @Test
    @WithMockUser("notReal@gmail.com")
    void Test023_GivenANonExistentUserWhenGettingLoggedUserThenBadRequestIsReturned()
            throws Exception {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        String errorMessage =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.ALL))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assertEquals("No user found for email: notReal@gmail.com", errorMessage);
    }

    @Test
    void Test024_GivenAUserStartingARecoveryPasswordProcessWhenUserExistsThenOkIsReturned() {
        HttpEntity<UserCreateDTO> request = new HttpEntity<>(userCreateDTO);

        val postResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, request, User.class);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        val confirmationToken =
                userRepository
                        .findById(postResponse.getBody().getId())
                        .get()
                        .getConfirmationToken();
        ConfirmationTokenDTO confirmationTokenDTO =
                ConfirmationTokenDTO.builder()
                        .confirmationToken(confirmationToken)
                        .id(postResponse.getBody().getId())
                        .build();
        confirmationTokenDTO.setConfirmationToken(confirmationToken);

        HttpEntity<ConfirmationTokenDTO> updatedRequest = new HttpEntity<>(confirmationTokenDTO);

        val postNewResponse =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, confirmationUrl),
                        HttpMethod.POST,
                        updatedRequest,
                        User.class);
        assertEquals(HttpStatus.OK, postNewResponse.getStatusCode());

        HttpEntity<PasswordRecoveryInitDTO> initRequest =
                new HttpEntity<>(
                        PasswordRecoveryInitDTO.builder().email(userCreateDTO.getEmail()).build());
        val postStartPasswordRecovery =
                restTemplate.exchange(
                        String.format("%s/%s", baseUrl, "/recover"),
                        HttpMethod.POST,
                        initRequest,
                        PasswordRecoveryInitDTO.class);

        assertEquals(HttpStatus.OK, postStartPasswordRecovery.getStatusCode());
    }
}
