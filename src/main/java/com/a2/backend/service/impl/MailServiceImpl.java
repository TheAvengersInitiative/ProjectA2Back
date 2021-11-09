package com.a2.backend.service.impl;

import com.a2.backend.entity.User;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.service.MailService;
import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Profile("notTest")
public class MailServiceImpl implements MailService {

    @Autowired private JavaMailSender emailsender;

    @Override
    public void sendConfirmationMail(User user) {
        String body =
                "Hello in order to confirm your account go to this link:"
                        + "<br>"
                        + "<br>"
                        + "http://localhost:3000/verify/"
                        + user.getId()
                        + '/'
                        + user.getConfirmationToken()
                        + "<br>"
                        + "<br>";
        this.sendEmail(user.getEmail(), "Account confirmation", body);
    }

    @Override
    public void sendForgotPasswordMail(User user) {
        String body =
                "Hello in order to change your password please follow this link: "
                        + "<br>"
                        + "<br>"
                        + "http://localhost:3000/forgot-password/"
                        + user.getId()
                        + '/'
                        + user.getConfirmationToken()
                        + "<br>"
                        + "<br>";
        this.sendEmail(user.getEmail(), "Password Recovery", body);
    }

    @Override
    public void sendNotificationMail(NotificationDTO notification) {
        String body =
                "You have a new "
                        + notification.getType()
                        + " notification!"
                        + '\n'
                        + '\n'
                        + "The Project A2 team";
        this.sendEmail(notification.getUserToNotify().getEmail(), "New Notification", body);
    }

    private void sendEmail(String mailTO, String subject, String content) {
        try {
            MimeMessage mimeMessage = emailsender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            FileSystemResource res =
                    new FileSystemResource(new File("src/main/resources/header.png"));
            String htmlMsg =
                    "<img src= 'cid:id1' height= 50 width= auto> <br> <h2>"
                            + subject
                            + "</h2>"
                            + "<p>"
                            + content
                            + "Best regards,<br>A2</p>";
            helper.setText(htmlMsg, true);
            helper.addInline("id1", res);
            helper.setTo(mailTO);
            helper.setSubject(subject);
            helper.setFrom("projectlab2avengerinitiative@gmail.com");
            emailsender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
