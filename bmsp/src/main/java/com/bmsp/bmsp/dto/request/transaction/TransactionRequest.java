package com.bmsp.bmsp.dto.request.transaction;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransactionRequest {
    @NotBlank
    private String accountNumber;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    private String description;

    public TransactionRequest() {
    }

    public TransactionRequest(String accountNumber, BigDecimal amount, String description) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.description = description;
    }

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}