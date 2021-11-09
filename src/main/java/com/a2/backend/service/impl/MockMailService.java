package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.service.MailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class MockMailService implements MailService {

    @Override
    public void sendConfirmationMail(User user) {
        System.out.println("MockMailServices:sendConfirmationMail called");
        // do not send mails during tests
    }

    @Override
    public void sendForgotPasswordMail(User user) {
        // do not send mails during tests
        System.out.println("MockMailServices:sendForgotPasswordMail called");
    }

    @Override
    public void sendNotificationMail(NotificationDTO notificationDetails) {
        // do not send mails during tests
        System.out.println("MockMailServices:sendNotificationMail called");
    }
}
