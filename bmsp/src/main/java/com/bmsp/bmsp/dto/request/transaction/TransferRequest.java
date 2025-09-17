// TransferRequest.java
package com.bmsp.bmsp.dto.request.transaction;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank
    private String fromAccountNumber;
    
    @NotBlank
    private String toAccountNumber;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    private String description;

    // Getters and setters
    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
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