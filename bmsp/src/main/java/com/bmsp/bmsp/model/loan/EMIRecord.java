package com.bmsp.bmsp.model.loan;

import com.bmsp.bmsp.model.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emi_records")
@Data
public class EMIRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private Integer installmentNumber;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private BigDecimal amount;

    private LocalDate paymentDate;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Column(nullable = false)
    private BigDecimal interestAmount;

    @Column(nullable = false)
    private BigDecimal remainingPrincipal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EMIStatus status = EMIStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public enum EMIStatus {
        PENDING,
        PAID,
        LATE,
        DEFAULTED
    }
}