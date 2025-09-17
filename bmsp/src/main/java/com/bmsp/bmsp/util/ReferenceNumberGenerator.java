// ReferenceNumberGenerator.java
package com.bmsp.bmsp.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ReferenceNumberGenerator {
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REFERENCE_LENGTH = 12;
    
    public String generateReferenceNumber() {
        StringBuilder sb = new StringBuilder(REFERENCE_LENGTH);
        Random random = new Random();
        
        for (int i = 0; i < REFERENCE_LENGTH; i++) {
            int index = random.nextInt(ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        
        return sb.toString();
    }
}