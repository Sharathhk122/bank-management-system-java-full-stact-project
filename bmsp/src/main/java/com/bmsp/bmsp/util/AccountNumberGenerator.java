// AccountNumberGenerator.java
package com.bmsp.bmsp.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    public String generateAccountNumber(String branchCode) {
        Random random = new Random();
        
        // Format: BranchCode (3 digits) + Random (10 digits) + Check digit
        String randomPart = String.format("%010d", random.nextInt(1_000_000_000));
        String accountNumberWithoutCheck = branchCode + randomPart;
        
        int checkDigit = calculateCheckDigit(accountNumberWithoutCheck);
        
        return accountNumberWithoutCheck + checkDigit;
    }

    private int calculateCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (10 - (sum % 10)) % 10;
    }
}