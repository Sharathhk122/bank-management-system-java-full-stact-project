// Account.java
package com.bmsp.bmsp.model.account;

import com.bmsp.bmsp.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    @Id
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String branchCode;

    @Column(nullable = false)
    private String branchName;

    private String ifscCode;

    // Additional fields
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private boolean allowOverdraft;
    private BigDecimal overdraftLimit;
}