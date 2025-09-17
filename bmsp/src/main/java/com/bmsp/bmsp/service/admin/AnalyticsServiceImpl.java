package com.bmsp.bmsp.service.admin;

import com.bmsp.bmsp.dto.response.analytics.*;
import com.bmsp.bmsp.model.loan.LoanStatus;
import com.bmsp.bmsp.repository.loan.LoanRepository;
import com.bmsp.bmsp.repository.transaction.TransactionRepository;
import com.bmsp.bmsp.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Override
    public RevenueAnalyticsResponse getRevenueAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Optional<BigDecimal> totalRevenueOpt = transactionRepository.calculateRevenue(startDateTime, endDateTime);
        Optional<BigDecimal> interestRevenueOpt = transactionRepository.calculateInterestRevenue(startDateTime, endDateTime);
        Optional<BigDecimal> feeRevenueOpt = transactionRepository.calculateFeeRevenue(startDateTime, endDateTime);
        
        BigDecimal totalRevenue = totalRevenueOpt.orElse(BigDecimal.ZERO);
        BigDecimal interestRevenue = interestRevenueOpt.orElse(BigDecimal.ZERO);
        BigDecimal feeRevenue = feeRevenueOpt.orElse(BigDecimal.ZERO);
        
        List<RevenueAnalyticsResponse.MonthlyRevenue> monthlyBreakdown = new ArrayList<>();
        
        monthlyBreakdown.add(RevenueAnalyticsResponse.MonthlyRevenue.builder()
                .month("January 2024")
                .revenue(new BigDecimal("15000.00"))
                .build());
        monthlyBreakdown.add(RevenueAnalyticsResponse.MonthlyRevenue.builder()
                .month("February 2024")
                .revenue(new BigDecimal("18000.00"))
                .build());
        
        return RevenueAnalyticsResponse.builder()
                .totalRevenue(totalRevenue)
                .interestRevenue(interestRevenue)
                .feeRevenue(feeRevenue)
                .monthlyBreakdown(monthlyBreakdown)
                .build();
    }

    @Override
    public CustomerGrowthAnalyticsResponse getCustomerGrowthAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        long totalCustomers = userRepository.count();
        long newCustomers = userRepository.countByCreatedAtBetween(startDateTime, endDateTime);
        long activeCustomers = userRepository.countByLastLoginAfter(LocalDateTime.now().minusMonths(1));
        
        List<CustomerGrowthAnalyticsResponse.MonthlyGrowth> monthlyGrowth = new ArrayList<>();
        monthlyGrowth.add(CustomerGrowthAnalyticsResponse.MonthlyGrowth.builder()
                .month("Jan 2024")
                .newCustomers(150L)
                .churnedCustomers(20L)
                .netGrowth(130L)
                .build());
        monthlyGrowth.add(CustomerGrowthAnalyticsResponse.MonthlyGrowth.builder()
                .month("Feb 2024")
                .newCustomers(180L)
                .churnedCustomers(15L)
                .netGrowth(165L)
                .build());
        
        return CustomerGrowthAnalyticsResponse.builder()
                .totalCustomers(totalCustomers)
                .newCustomers(newCustomers)
                .activeCustomers(activeCustomers)
                .monthlyGrowth(monthlyGrowth)
                .build();
    }

    @Override
    public LoanPerformanceAnalyticsResponse getLoanPerformanceAnalytics(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalPortfolio = new BigDecimal("5000000.00");
        BigDecimal averageLoanSize = new BigDecimal("25000.00");
        BigDecimal defaultRate = calculateDefaultRate();
        BigDecimal recoveryRate = calculateRecoveryRate();
        
        List<LoanPerformanceAnalyticsResponse.LoanTypePerformance> performanceByType = new ArrayList<>();
        performanceByType.add(LoanPerformanceAnalyticsResponse.LoanTypePerformance.builder()
                .loanType("PERSONAL")
                .totalLoans(150L)
                .totalAmount(new BigDecimal("750000.00"))
                .defaultRate(new BigDecimal("2.5"))
                .build());
        performanceByType.add(LoanPerformanceAnalyticsResponse.LoanTypePerformance.builder()
                .loanType("HOME")
                .totalLoans(80L)
                .totalAmount(new BigDecimal("2000000.00"))
                .defaultRate(new BigDecimal("1.2"))
                .build());
        
        return LoanPerformanceAnalyticsResponse.builder()
                .totalPortfolio(totalPortfolio)
                .averageLoanSize(averageLoanSize)
                .defaultRate(defaultRate)
                .recoveryRate(recoveryRate)
                .performanceByType(performanceByType)
                .build();
    }

    @Override
    public TransactionPatternAnalyticsResponse getTransactionPatternAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Optional<BigDecimal> avgTransactionValueOpt = transactionRepository.findAverageTransactionValue(startDateTime, endDateTime);
        BigDecimal averageTransactionValue = avgTransactionValueOpt.orElse(BigDecimal.ZERO);
        
        Integer peakHour = 12;
        String mostCommonType = "TRANSFER";
        
        List<TransactionPatternAnalyticsResponse.HourlyPattern> hourlyPatterns = new ArrayList<>();
        for (int i = 9; i <= 17; i++) {
            hourlyPatterns.add(TransactionPatternAnalyticsResponse.HourlyPattern.builder()
                    .hour(i)
                    .transactionCount(100L + i * 10L)
                    .totalAmount(new BigDecimal(5000 + i * 1000))
                    .build());
        }
        
        List<TransactionPatternAnalyticsResponse.WeeklyPattern> weeklyPatterns = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            weeklyPatterns.add(TransactionPatternAnalyticsResponse.WeeklyPattern.builder()
                    .dayOfWeek(days[i])
                    .transactionCount(500L + i * 50L)
                    .totalAmount(new BigDecimal(25000 + i * 5000))
                    .build());
        }
        
        return TransactionPatternAnalyticsResponse.builder()
                .averageTransactionValue(averageTransactionValue)
                .peakHour(peakHour)
                .mostCommonType(mostCommonType)
                .hourlyPatterns(hourlyPatterns)
                .weeklyPatterns(weeklyPatterns)
                .build();
    }

    @Override
    public List<GeographicDistributionResponse> getGeographicDistribution() {
        List<GeographicDistributionResponse> distribution = new ArrayList<>();
        distribution.add(GeographicDistributionResponse.builder()
                .region("North America")
                .customerCount(1500L)
                .totalBalance(new BigDecimal("25000000.00"))
                .build());
        distribution.add(GeographicDistributionResponse.builder()
                .region("Europe")
                .customerCount(1200L)
                .totalBalance(new BigDecimal("18000000.00"))
                .build());
        distribution.add(GeographicDistributionResponse.builder()
                .region("Asia")
                .customerCount(2000L)
                .totalBalance(new BigDecimal("30000000.00"))
                .build());
        
        return distribution;
    }

    @Override
    public RiskAssessmentResponse getRiskAssessment() {
        int highRiskAccounts = (int) userRepository.countByRiskScoreGreaterThan(80);
        int suspiciousTransactions = transactionRepository.countSuspiciousTransactionsLast30Days();
        
        BigDecimal potentialExposure = new BigDecimal("500000.00");
        
        List<RiskAssessmentResponse.RiskFactor> topRiskFactors = new ArrayList<>();
        topRiskFactors.add(RiskAssessmentResponse.RiskFactor.builder()
                .factor("High transaction frequency")
                .count(45)
                .exposure(new BigDecimal("120000.00"))
                .build());
        topRiskFactors.add(RiskAssessmentResponse.RiskFactor.builder()
                .factor("Large cash withdrawals")
                .count(32)
                .exposure(new BigDecimal("85000.00"))
                .build());
        
        BigDecimal overallRiskScore = calculateOverallRiskScore(highRiskAccounts, suspiciousTransactions, potentialExposure);
        
        return RiskAssessmentResponse.builder()
                .highRiskAccounts(highRiskAccounts)
                .suspiciousTransactions(suspiciousTransactions)
                .potentialExposure(potentialExposure)
                .topRiskFactors(topRiskFactors)
                .overallRiskScore(overallRiskScore)
                .build();
    }

    @Override
    public RealTimeMetricsResponse getRealTimeMetrics() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        
        int activeSessions = userRepository.countActiveSessions(thirtyMinutesAgo);
        
        Optional<BigDecimal> transactionsLastHourOpt = transactionRepository.sumTransactionsLastHour();
        BigDecimal transactionsLastHour = transactionsLastHourOpt.orElse(BigDecimal.ZERO);
        
        int newUsersLastHour = userRepository.countByCreatedAtAfter(oneHourAgo);
        BigDecimal systemUptime = BigDecimal.valueOf(99.95);
        int responseTimeMs = 150;

        return RealTimeMetricsResponse.builder()
                .activeSessions(activeSessions)
                .transactionsLastHour(transactionsLastHour)
                .newUsersLastHour(newUsersLastHour)
                .systemUptime(systemUptime)
                .responseTimeMs(responseTimeMs)
                .build();
    }

    private BigDecimal calculateDefaultRate() {
        long defaultedLoans = loanRepository.countByStatus(LoanStatus.DEFAULTED);
        long totalDisbursedLoans = loanRepository.countByStatus(LoanStatus.DISBURSED);
        
        if (totalDisbursedLoans == 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(defaultedLoans)
                .divide(BigDecimal.valueOf(totalDisbursedLoans), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateRecoveryRate() {
        BigDecimal totalRecovered = new BigDecimal("50000.00");
        BigDecimal totalDefaulted = new BigDecimal("75000.00");
        
        if (totalDefaulted.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalRecovered.divide(totalDefaulted, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateOverallRiskScore(int highRiskAccounts, int suspiciousTransactions, BigDecimal potentialExposure) {
        BigDecimal accountRisk = BigDecimal.valueOf(highRiskAccounts).multiply(new BigDecimal("0.4"));
        BigDecimal transactionRisk = BigDecimal.valueOf(suspiciousTransactions).multiply(new BigDecimal("0.3"));
        BigDecimal exposureRisk = potentialExposure.divide(new BigDecimal("1000000"), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.3"));
        
        return accountRisk.add(transactionRisk).add(exposureRisk)
                .setScale(2, RoundingMode.HALF_UP);
    }
}