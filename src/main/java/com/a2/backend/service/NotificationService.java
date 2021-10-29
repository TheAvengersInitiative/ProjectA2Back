package com.a2.backend.service;

import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;

public interface NotificationService {
    NotificationDTO createNotification(NotificationCreateDTO notificationCreateDTO);
}
