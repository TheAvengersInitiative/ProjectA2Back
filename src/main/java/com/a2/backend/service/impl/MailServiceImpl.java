package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class MailServiceImpl implements MailService {

    @Autowired private JavaMailSender emailsender;

    @Override
    public void sendConfirmationMail(User user) {
        String body =
                "Hello in order to confirm your account go to this link: "
                        + '\n'
                        + "http://localhost:3000/verify/"
                        + user.getId()
                        + '/'
                        + user.getConfirmationToken()
                        + '\n'
                        + '\n'
                        + "The Project A2 team";
        this.sendEmail(user.getEmail(), "Account confirmation", body);
    }

    @Override
    public void sendForgotPasswordMail(User user) {
        String body =
                "Hello in order to change your password please follow this link: "
                        + '\n'
                        + "http://localhost:3000/forgot-password/"
                        + user.getId()
                        + '/'
                        + user.getConfirmationToken()
                        + '\n'
                        + '\n'
                        + "The Project A2 team";
        this.sendEmail(user.getEmail(), "Password Recovery", body);
    }

    private void sendEmail(String mailTO, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailTO);
        message.setFrom("projectlab2avengerinitiative@gmail.com");
        message.setSubject(subject);
        message.setText(content);
        emailsender.send(message);
    }
}
