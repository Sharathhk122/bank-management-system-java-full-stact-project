package com.bmsp.bmsp.controller.admin;

import com.bmsp.bmsp.dto.response.analytics.*;
import com.bmsp.bmsp.service.admin.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueAnalyticsResponse> getRevenueAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        RevenueAnalyticsResponse analytics = analyticsService.getRevenueAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/customer-growth")
    public ResponseEntity<CustomerGrowthAnalyticsResponse> getCustomerGrowthAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        CustomerGrowthAnalyticsResponse analytics = analyticsService.getCustomerGrowthAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/loan-performance")
    public ResponseEntity<LoanPerformanceAnalyticsResponse> getLoanPerformanceAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LoanPerformanceAnalyticsResponse analytics = analyticsService.getLoanPerformanceAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/transaction-patterns")
    public ResponseEntity<TransactionPatternAnalyticsResponse> getTransactionPatternAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        TransactionPatternAnalyticsResponse analytics = analyticsService.getTransactionPatternAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/geographic-distribution")
    public ResponseEntity<List<GeographicDistributionResponse>> getGeographicDistribution() {
        List<GeographicDistributionResponse> distribution = analyticsService.getGeographicDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/risk-assessment")
    public ResponseEntity<RiskAssessmentResponse> getRiskAssessment() {
        RiskAssessmentResponse assessment = analyticsService.getRiskAssessment();
        return ResponseEntity.ok(assessment);
    }

    @GetMapping("/real-time-metrics")
    public ResponseEntity<RealTimeMetricsResponse> getRealTimeMetrics() {
        RealTimeMetricsResponse metrics = analyticsService.getRealTimeMetrics();
        return ResponseEntity.ok(metrics);
    }
}