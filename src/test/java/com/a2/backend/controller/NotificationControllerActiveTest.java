package com.a2.backend.controller;

import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NotificationControllerActiveTest extends AbstractControllerTest {

    @Autowired MockMvc mvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired NotificationRepository notificationRepository;

    @Autowired UserService userService;

    private final String baseUrl = "/notification";

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test001_NotificationControllerShouldReturnLoggedUserNotificationsAndHttpOkTest()
            throws Exception {

        String contentAsString =
                mvc.perform(MockMvcRequestBuilders.get(baseUrl).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val notifications = objectMapper.readValue(contentAsString, NotificationDTO[].class);

        assertNotNull(notifications);
        assertEquals(3, notifications.length);
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test002_NotificationControllerWhenMarkingNotificationAsReadThenHttpOkIsReturned()
            throws Exception {
        UUID id =
                notificationRepository
                        .findAllByUserToNotify(userService.getLoggedUser())
                        .get(0)
                        .getId();

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.put(baseUrl + "/" + id)
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val notification = objectMapper.readValue(contentAsString, NotificationDTO.class);

        assertTrue(notification.isSeen());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test002_NotificationControllerShouldReturnLoggedUserFirstFiveNotificationsAndHttpOkTest()
            throws Exception {

        String contentAsString =
                mvc.perform(
                                MockMvcRequestBuilders.get(baseUrl + "/first-five")
                                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        val notifications = objectMapper.readValue(contentAsString, NotificationDTO[].class);

        assertNotNull(notifications);
        assertEquals(5, notifications.length);
    }
}
