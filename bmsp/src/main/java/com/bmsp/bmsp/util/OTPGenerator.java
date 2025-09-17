package com.bmsp.bmsp.util;

import java.util.Random;

public class OTPGenerator {
    private static final String NUMBERS = "0123456789";
    private static final Random random = new Random();

    public static String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return otp.toString();
    }
}