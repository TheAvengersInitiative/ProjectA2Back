package com.a2.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.a2.backend.constants.PrivacyConstant;
import com.a2.backend.entity.User;
import com.a2.backend.model.UserPrivacyDTO;
import com.a2.backend.model.UserProfileDTO;
import com.a2.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
public class UserControllerActiveTest extends AbstractControllerTest {
    @Autowired MockMvc mvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;

    private final String baseUrl = "/user";

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test001_UserControllerWhenUpdatingUserPreferencesThenTheyStatusIsOkAndUpdatedUserIsReturned()
                    throws Exception {
        UserPrivacyDTO privacyDTO =
                UserPrivacyDTO.builder()
                        .tagsPrivacy(PrivacyConstant.PRIVATE)
                        .collaboratedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .languagesPrivacy(PrivacyConstant.PRIVATE)
                        .ownedProjectsPrivacy(PrivacyConstant.PRIVATE)
                        .build();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl + "/privacy")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(privacyDTO))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        User updatedUser = objectMapper.readValue(contentAsString, User.class);

        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getTagsPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getLanguagesPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getCollaboratedProjectsPrivacy());
        assertEquals(PrivacyConstant.PRIVATE, updatedUser.getOwnedProjectsPrivacy());
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void
            Test002_UserControllerWhenGettingUserProfileThenStatusIsOkAndProfileWithPublicFieldsIsReturned()
                    throws Exception {
        User user = userRepository.findByEmail("rodrigo.pazos@ing.austral.edu.ar").get();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format("%s/%s", baseUrl, user.getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        UserProfileDTO userProfile = objectMapper.readValue(contentAsString, UserProfileDTO.class);

        assertEquals(user.getNickname(), userProfile.getNickname());
        assertEquals(user.getBiography(), userProfile.getBiography());
        assertNotNull(userProfile.getPreferredTags());
        assertTrue(user.getPreferredTags().containsAll(userProfile.getPreferredTags()));
        assertNotNull(userProfile.getOwnedProjects());
        assertEquals(4, userProfile.getOwnedProjects().size());
        assertNotNull(userProfile.getCollaboratedProjects());
        assertEquals(2, userProfile.getCollaboratedProjects().size());
        assertNotNull(userProfile.getPreferredLanguages());
        assertTrue(user.getPreferredLanguages().containsAll(userProfile.getPreferredLanguages()));
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void
            Test003_UserControllerWhenGettingUserProfileThenStatusIsOkAndProfileWithNullOnPrivateFieldsIsReturned()
                    throws Exception {
        User user = userRepository.findByEmail("agustin.ayerza@ing.austral.edu.ar").get();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format("%s/%s", baseUrl, user.getId()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        UserProfileDTO userProfile = objectMapper.readValue(contentAsString, UserProfileDTO.class);

        assertEquals(user.getNickname(), userProfile.getNickname());
        assertEquals(user.getBiography(), userProfile.getBiography());
        assertNotNull(userProfile.getPreferredTags());
        assertTrue(user.getPreferredTags().containsAll(userProfile.getPreferredTags()));
        assertNotNull(userProfile.getOwnedProjects());
        assertEquals(3, userProfile.getOwnedProjects().size());
        assertNull(userProfile.getCollaboratedProjects());
        assertNull(userProfile.getPreferredLanguages());
    }

    @Test
    @WithMockUser(username = "rodrigo.pazos@ing.austral.edu.ar")
    void Test004_UserControllerWhenGettingUserProfileForNonExistentUserThenBadRequestIsReturned()
            throws Exception {
        String errorMessage =
                mvc.perform(
                                MockMvcRequestBuilders.get(
                                                String.format("%s/%s", baseUrl, UUID.randomUUID()))
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        assert (Objects.requireNonNull(errorMessage).contains("There is no user with id"));
    }
}
