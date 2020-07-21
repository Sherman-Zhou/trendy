package com.joinbe.service.impl;

import com.joinbe.domain.Staff;
import com.joinbe.domain.SystemUser;
import com.joinbe.service.MailService;
import io.github.jhipster.config.JHipsterProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailServiceImpl implements MailService {

    private final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailServiceImpl(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
                           MessageSource messageSource, SpringTemplateEngine templateEngine) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Override
   // @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);
        if(StringUtils.isEmpty(to)){
            log.warn("the mail to is blank: {}", to);
            return;
        }
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());

            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Override
    //@Async
    public void sendEmailFromTemplate(Staff staff, String templateName, String titleKey) {
        if (staff.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", staff.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(staff.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, staff);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, null, locale);
        sendEmail(staff.getEmail(), subject, content, false, true);
    }

    @Override
    //@Async
    public void sendEmailFromTemplate(SystemUser staff, String templateName, String titleKey) {
        if (staff.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", staff.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag(staff.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, staff);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, null, locale);
        sendEmail(staff.getEmail(), subject, content, false, true);
    }

    @Override
    // @Async
    public void sendActivationEmail(Staff staff) {
        log.debug("Sending activation email to '{}'", staff.getEmail());
        sendEmailFromTemplate(staff, "mail/activationEmail", "email.activation.title");
    }

    @Override
    // @Async
    public void sendEmailChangeEmail(Staff staff) {
        log.debug("Sending change email notification to '{}'", staff.getEmail());

        Locale locale = Locale.forLanguageTag(staff.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, staff);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process("mail/changeEmail", context);
        String subject = messageSource.getMessage("email.change.title", null, null, locale);
        sendEmail(staff.getOldEmail(), subject, content, false, true);
    }

    @Override
    // @Async
    public void sendEmailChangeEmail(SystemUser staff) {
        log.debug("Sending change email notification to '{}'", staff.getEmail());

        Locale locale = Locale.forLanguageTag(staff.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, staff);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process("mail/changeEmail", context);
        String subject = messageSource.getMessage("email.change.title", null, null, locale);
        sendEmail(staff.getOldEmail(), subject, content, false, true);
    }


    @Override
    //  @Async
    public void sendPasswordResetMail(Staff staff) {
        log.debug("Sending password reset email to '{}'", staff.getEmail());
        sendEmailFromTemplate(staff, "mail/passwordResetEmail", "email.reset.title");
    }

    @Override
    //  @Async
    public void sendPasswordResetMail(SystemUser staff) {
        log.debug("Sending password reset email to '{}'", staff.getEmail());
        sendEmailFromTemplate(staff, "mail/passwordResetEmail", "email.reset.title");
    }
}
