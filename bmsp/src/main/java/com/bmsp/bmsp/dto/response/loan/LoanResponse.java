package com.bmsp.bmsp.dto.response.loan;

import com.bmsp.bmsp.model.loan.LoanStatus;
import com.bmsp.bmsp.model.loan.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private Long id;
    private String loanAccountNumber;
    private LoanType loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private LoanStatus status;
    private BigDecimal emiAmount;
    private BigDecimal totalPayableAmount;
    private BigDecimal paidAmount;
    private BigDecimal recoveredAmount;
    private String rejectionReason;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}