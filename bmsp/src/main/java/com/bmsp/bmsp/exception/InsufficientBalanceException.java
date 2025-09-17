// InsufficientBalanceException.java
package com.bmsp.bmsp.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}