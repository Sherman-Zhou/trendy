package com.joinbe.service;

import com.joinbe.domain.Staff;
import com.joinbe.domain.SystemUser;

public interface MailService {

    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailFromTemplate(Staff staff, String templateName, String titleKey);

    void sendActivationEmail(Staff staff);

    void sendEmailChangeEmail(Staff staff);

    void sendEmailChangeEmail(SystemUser staff);

    void sendCreationEmail(Staff staff);

    void sendPasswordResetMail(Staff staff);
}
