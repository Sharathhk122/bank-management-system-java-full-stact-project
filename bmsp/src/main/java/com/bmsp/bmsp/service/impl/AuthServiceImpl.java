package com.bmsp.bmsp.service.impl;

import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.repository.auth.UserRepository;
import com.bmsp.bmsp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    private final Map<String, String> otpStorage = new HashMap<>();

    @Override
    public ResponseEntity<?> verifyEmail(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setEmailVerified(true);
            userRepository.save(user);
            otpStorage.remove(email);
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or expired");
        }
    }

    @Override
    public void saveOTP(String email, String otp) {
        otpStorage.put(email, otp);
    }
}