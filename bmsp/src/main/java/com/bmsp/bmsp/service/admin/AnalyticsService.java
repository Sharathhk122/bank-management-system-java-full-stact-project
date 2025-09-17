package com.bmsp.bmsp.service.admin;

import com.bmsp.bmsp.dto.response.analytics.*;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    RevenueAnalyticsResponse getRevenueAnalytics(LocalDate startDate, LocalDate endDate);
    CustomerGrowthAnalyticsResponse getCustomerGrowthAnalytics(LocalDate startDate, LocalDate endDate);
    LoanPerformanceAnalyticsResponse getLoanPerformanceAnalytics(LocalDate startDate, LocalDate endDate);
    TransactionPatternAnalyticsResponse getTransactionPatternAnalytics(LocalDate startDate, LocalDate endDate);
    List<GeographicDistributionResponse> getGeographicDistribution();
    RiskAssessmentResponse getRiskAssessment();
    RealTimeMetricsResponse getRealTimeMetrics();
}