package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
@Data
@Builder
public class AdminDashboardStats {
    private long totalUsers;
    private long activeUsers;
    private long totalAccounts;
    private long activeAccounts;
    private BigDecimal totalBalance;
    private BigDecimal todayTransactionVolume;
    private long pendingLoans;
    private BigDecimal totalLoanAmount; // Added this field
    private BigDecimal totalOutstandingAmount; // Added this field
}