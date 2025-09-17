package com.bmsp.bmsp.service;

public interface EmailService {
    void sendVerificationEmail(String email, String otp);
    void sendWelcomeEmail(String email, String fullName);
}