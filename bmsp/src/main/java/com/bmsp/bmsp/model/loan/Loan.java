package com.bmsp.bmsp.model.loan;

import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loanAccountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "accountNumber", nullable = false)
    private Account linkedAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = true)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'PENDING'")
    private LoanStatus status = LoanStatus.PENDING;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal emiAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalPayableAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal recoveredAmount = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    private String rejectionReason;

    private String approvedBy;

    private LocalDateTime approvedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EMIRecord> emiRecords;

    @PrePersist
    public void generateLoanAccountNumber() {
        if (this.loanAccountNumber == null) {
            this.loanAccountNumber = "LN" + System.currentTimeMillis();
        }
    }

    // Business methods
    public void approve(LocalDate startDate, String approvedBy) {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending loans can be approved");
        }
        this.status = LoanStatus.APPROVED;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(this.tenureMonths);
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        
        // Auto-disburse when approved
        this.status = LoanStatus.DISBURSED;
    }

    public void reject(String rejectionReason, String approvedBy) {
        if (this.status != LoanStatus.PENDING) {
            throw new IllegalStateException("Only pending loans can be rejected");
        }
        this.status = LoanStatus.REJECTED;
        this.rejectionReason = rejectionReason;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }
}