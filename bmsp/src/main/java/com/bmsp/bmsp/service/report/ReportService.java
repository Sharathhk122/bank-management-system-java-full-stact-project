package com.bmsp.bmsp.service.report;

import java.time.LocalDate;

public interface ReportService {
    byte[] generateTransactionReportPDF(LocalDate startDate, LocalDate endDate, String transactionType);
    byte[] generateTransactionReportExcel(LocalDate startDate, LocalDate endDate, String transactionType);
    byte[] generateLoanReportPDF(LocalDate startDate, LocalDate endDate, String loanStatus);
    byte[] generateCustomerReportPDF(LocalDate startDate, LocalDate endDate);
    byte[] generateFinancialSummaryReport(int year, int month);
}