package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class LoanPerformanceAnalyticsResponse {
    private BigDecimal totalPortfolio;
    private BigDecimal averageLoanSize;
    private BigDecimal defaultRate;
    private BigDecimal recoveryRate;
    private List<LoanTypePerformance> performanceByType;
    
    @Data
    @Builder
    public static class LoanTypePerformance {
        private String loanType;
        private long totalLoans;
        private BigDecimal totalAmount;
        private BigDecimal defaultRate;
    }
}