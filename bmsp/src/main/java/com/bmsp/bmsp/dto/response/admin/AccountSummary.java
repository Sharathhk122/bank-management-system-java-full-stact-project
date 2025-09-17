package com.bmsp.bmsp.dto.response.admin;

import com.bmsp.bmsp.model.account.AccountStatus;
import com.bmsp.bmsp.model.account.AccountType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountSummary {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
}