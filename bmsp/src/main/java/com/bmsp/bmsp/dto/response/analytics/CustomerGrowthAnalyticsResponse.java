package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CustomerGrowthAnalyticsResponse {
    private long totalCustomers;
    private long newCustomers;
    private long activeCustomers;
    private List<MonthlyGrowth> monthlyGrowth;
    
    @Data
    @Builder
    public static class MonthlyGrowth {
        private String month;
        private long newCustomers;
        private long churnedCustomers;
        private long netGrowth;
    }
}