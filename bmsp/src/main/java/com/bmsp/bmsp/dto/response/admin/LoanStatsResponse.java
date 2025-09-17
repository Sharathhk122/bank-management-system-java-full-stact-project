package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanStatsResponse {
    private long totalLoans;
    private long pendingLoans;
    private long approvedLoans;
    private long disbursedLoans;
    private long rejectedLoans;
    private long closedLoans;
    private long defaultedLoans; // Added this field
    private BigDecimal totalLoanAmount;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal defaultedAmount; // Added this field
    private BigDecimal recoveredAmount;
}