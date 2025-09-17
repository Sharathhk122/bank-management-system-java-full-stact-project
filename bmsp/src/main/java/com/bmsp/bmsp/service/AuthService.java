package com.bmsp.bmsp.service;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> verifyEmail(String email, String otp);
    void saveOTP(String email, String otp);
}