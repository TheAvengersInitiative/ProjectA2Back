package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.NotificationDTO;

public interface MailService {

    public void sendConfirmationMail(User user);

    public void sendForgotPasswordMail(User user);

    public void sendNotificationMail(NotificationDTO notificationDetails);
}
