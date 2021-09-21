package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.repository.UserRepository;
import com.a2.backend.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired private JavaMailSender emailsender;

    private final UserRepository userRepository;

    public MailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void sendConfirmationMail(User user) {
        this.sendEmail(
                user.getEmail(),
                "Account confirmation",
                "Hello in order to confirm your account press this link: "
                        + "http://localhost:3000/user/confirm?token="
                        + user.getConfirmationToken());
    }

    @Override
    public void sendForgotPasswordMail(User user) {
        this.sendEmail(
                user.getEmail(),
                "Password Recovery",
                "Hello in order to confirm your account press this link: "
                        + "http://localhost:3000/user/recover/forgot-password?token="
                        + user.getPasswordRecoveryToken());
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
