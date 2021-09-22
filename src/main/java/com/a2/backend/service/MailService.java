package com.a2.backend.service;

import com.a2.backend.entity.User;

public interface MailService {

    public void sendConfirmationMail(User user);

    public void sendForgotPasswordMail(User user);
}
