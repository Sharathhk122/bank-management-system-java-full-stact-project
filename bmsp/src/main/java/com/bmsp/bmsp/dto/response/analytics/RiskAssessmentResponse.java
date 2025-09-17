package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class RiskAssessmentResponse {
    private int highRiskAccounts;
    private int suspiciousTransactions;
    private BigDecimal potentialExposure;
    private List<RiskFactor> topRiskFactors;
    private BigDecimal overallRiskScore;
    
    @Data
    @Builder
    public static class RiskFactor {
        private String factor;
        private int count;
        private BigDecimal exposure;
    }
}