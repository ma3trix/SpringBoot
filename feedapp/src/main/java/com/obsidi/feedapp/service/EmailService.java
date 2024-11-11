package com.obsidi.feedapp.service;

import com.obsidi.feedapp.jpa.User;
import com.obsidi.feedapp.provider.ResourceProvider;
import com.obsidi.feedapp.security.JwtService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ResourceProvider provider;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    private void sendEmail(User user, String clientParam, String templateName, String emailSubject, long expiration) {
        try {
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("client", this.provider.getClientUrl());
            context.setVariable("param", clientParam);
            context.setVariable("token", this.jwtService.generateJwtToken(user.getUsername(), expiration));

            String process = this.templateEngine.process(templateName, context);

            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(this.emailFrom, "FeedApp - Obsidi Academy");
            helper.setSubject(emailSubject);
            helper.setText(process, true);
            helper.setTo(user.getEmailId());

            this.javaMailSender.send(mimeMessage);
            this.logger.debug("Email Sent, {} ", user.getEmailId());

        } catch (Exception ex) {
            this.logger.error("Error while Sending Email, Username: " + user.getUsername(), ex);
        }
    }

    @Async
    public void sendResetPasswordEmail(User user) {

        this.sendEmail(user, this.provider.getClientResetParam(), "reset_password", "Reset your password",
                this.provider.getClientResetExpiration());
    }

    @Async
    public void sendVerificationEmail(User user) {
        logger.info("Attempting to send verification email to user: {}", user.getEmailId());
        this.sendEmail(user, this.provider.getClientVerifyParam(), "verify_email",
                String.format("Welcome %s %s", user.getFirstName(), user.getLastName()),
                this.provider.getClientVerifyExpiration());
        logger.info("Verification email sent successfully.");
    }
}