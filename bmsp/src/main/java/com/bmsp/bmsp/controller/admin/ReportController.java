package com.bmsp.bmsp.controller.admin;

import com.bmsp.bmsp.service.report.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/transactions/pdf")
    public ResponseEntity<byte[]> generateTransactionReportPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String transactionType) {
        try {
            byte[] pdfBytes = reportService.generateTransactionReportPDF(startDate, endDate, transactionType);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Failed to generate transaction PDF report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/transactions/excel")
    public ResponseEntity<byte[]> generateTransactionReportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String transactionType) {
        try {
            byte[] excelBytes = reportService.generateTransactionReportExcel(startDate, endDate, transactionType);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transaction-report.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Failed to generate transaction Excel report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/loans/pdf")
    public ResponseEntity<byte[]> generateLoanReportPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String loanStatus) {
        try {
            byte[] pdfBytes = reportService.generateLoanReportPDF(startDate, endDate, loanStatus);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loan-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Failed to generate loan PDF report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/customers/pdf")
    public ResponseEntity<byte[]> generateCustomerReportPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            byte[] pdfBytes = reportService.generateCustomerReportPDF(startDate, endDate);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customer-report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Failed to generate customer PDF report: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/financial-summary")
    public ResponseEntity<byte[]> generateFinancialSummaryReport(
            @RequestParam int year,
            @RequestParam int month) {
        try {
            byte[] pdfBytes = reportService.generateFinancialSummaryReport(year, month);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=financial-summary.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Failed to generate financial summary report: " + e.getMessage()).getBytes());
        }
    }
}