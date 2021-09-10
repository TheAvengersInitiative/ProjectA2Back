package com.a2.backend.service;

import com.a2.backend.entity.User;

public interface MailService {

    public void sendConfirmationMail(User user, String confirmationToken);

    public void sendForgotPasswordMail(String mail);
}
