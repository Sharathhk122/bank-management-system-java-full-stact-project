package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RevenueAnalyticsResponse {
    private BigDecimal totalRevenue;
    private BigDecimal interestRevenue;
    private BigDecimal feeRevenue;
    private List<MonthlyRevenue> monthlyBreakdown;
    
    @Data
    @Builder
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal revenue;
    }
}