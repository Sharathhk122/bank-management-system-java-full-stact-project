package com.bmsp.bmsp.service.impl;

import com.bmsp.bmsp.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url}")
    private String baseUrl;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendVerificationEmail(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("NextGen Finance Hub <" + fromEmail + ">");
            helper.setTo(email);
            helper.setSubject("🔒 Your NextGen Finance Hub Verification Code: " + otp);
            
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("baseUrl", baseUrl);
            
            String htmlContent = templateEngine.process("emails/otp-verification", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(String email, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("NextGen Finance Hub <" + fromEmail + ">");
            helper.setTo(email);
            helper.setSubject("🎉 Welcome to NextGen Finance Hub - Your Account is Ready!");
            
            Context context = new Context();
            context.setVariable("fullName", fullName.split(" ")[0]);
            context.setVariable("baseUrl", baseUrl);
            
            String htmlContent = templateEngine.process("emails/welcome", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }
}