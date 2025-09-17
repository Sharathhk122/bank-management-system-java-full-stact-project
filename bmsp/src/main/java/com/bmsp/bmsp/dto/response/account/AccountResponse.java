// AccountResponse.java
package com.bmsp.bmsp.dto.response.account;

import com.bmsp.bmsp.model.account.AccountStatus;
import com.bmsp.bmsp.model.account.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String branchCode;
    private String branchName;
    private String ifscCode;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private Boolean allowOverdraft;
    private BigDecimal overdraftLimit;
}