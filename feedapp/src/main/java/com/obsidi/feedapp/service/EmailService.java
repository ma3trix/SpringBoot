package com.obsidi.feedapp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import com.obsidi.feedapp.provider.ResourceProvider;
import com.obsidi.feedapp.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;
import com.obsidi.feedapp.jpa.User;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Autowired
    JwtService jwtService;

    @Autowired
    ResourceProvider provider;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    @Async
    public void sendVerificationEmail(User user) {
        this.sendEmail(user, this.provider.getClientVerifyParam(), "verify_email",
                String.format("Welcome %s %s", user.getFirstName(), user.getLastName()),
                this.provider.getClientVerifyExpiration());
    }

    private void sendEmail(User user, String clientParam, String templateName, String emailSubject, long expiration) {
        try {
            /* Collect Data for the Email HTML generation */
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("client", this.provider.getClientUrl());
            context.setVariable("param", clientParam);
            context.setVariable("token", this.jwtService.generateJwtToken(user.getUsername(), expiration));

            /* Process Email HTML Template */
            String process = this.templateEngine.process(templateName, context);

            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            /* Set Email Information */
            helper.setFrom(this.emailFrom, "FeedApp - Obsidi Academy");
            helper.setSubject(emailSubject);
            helper.setText(process, true);
            helper.setTo(user.getEmailId());

            /* Send Email */
            this.javaMailSender.send(mimeMessage);

            this.logger.debug("Email Sent, {} ", user.getEmailId());
        } catch (Exception ex) {
            this.logger.error("Error while Sending Email, Username: " + user.getUsername(), ex);
        }
    }
}