package com.joinbe.service;

import com.joinbe.domain.User;

public interface MailService {

    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailFromTemplate(User user, String templateName, String titleKey);

    void sendActivationEmail(User user);

    void sendEmailChangeEmail(User user);

    void sendCreationEmail(User user);

    void sendPasswordResetMail(User user);
}
