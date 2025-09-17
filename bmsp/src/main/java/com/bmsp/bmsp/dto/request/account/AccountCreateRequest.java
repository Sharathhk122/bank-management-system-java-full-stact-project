// AccountCreateRequest.java
package com.bmsp.bmsp.dto.request.account;

import com.bmsp.bmsp.model.account.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    @NotNull
    private AccountType accountType;

    private BigDecimal initialDeposit;

    @NotNull
    private String branchCode;

    @NotNull
    private String branchName;

    private String ifscCode;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private Boolean allowOverdraft;
    private BigDecimal overdraftLimit;
}