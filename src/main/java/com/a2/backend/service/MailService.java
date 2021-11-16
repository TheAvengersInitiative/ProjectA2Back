package com.a2.backend.service;

import com.a2.backend.entity.User;
import com.a2.backend.model.NotificationDTO;

public interface MailService {

    void sendConfirmationMail(User user);

    void sendForgotPasswordMail(User user);

    void sendNotificationMail(NotificationDTO notification);
}
