package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TransactionPatternAnalyticsResponse {
    private BigDecimal averageTransactionValue;
    private int peakHour;
    private String mostCommonType;
    private List<HourlyPattern> hourlyPatterns;
    private List<WeeklyPattern> weeklyPatterns;
    
    @Data
    @Builder
    public static class HourlyPattern {
        private int hour;
        private long transactionCount;
        private BigDecimal totalAmount;
    }
    
    @Data
    @Builder
    public static class WeeklyPattern {
        private String dayOfWeek;
        private long transactionCount;
        private BigDecimal totalAmount;
    }
}