package com.bmsp.bmsp.service.report;

import com.bmsp.bmsp.dto.response.admin.TransactionAnalyticsResponse;
import com.bmsp.bmsp.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class ExcelGeneratorService {

    private final AdminService adminService;

    public byte[] generateTransactionReportExcel(LocalDate startDate, LocalDate endDate, String transactionType) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Transaction Report");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Type", "Account", "Amount", "Reference", "Description"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // Add summary section
            addSummarySection(workbook, sheet, startDate, endDate);
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    private void addSummarySection(Workbook workbook, Sheet sheet, LocalDate startDate, LocalDate endDate) {
        TransactionAnalyticsResponse analytics = adminService.getTransactionAnalytics(startDate, endDate);
        
        Row summaryHeaderRow = sheet.createRow(2);
        Cell summaryHeaderCell = summaryHeaderRow.createCell(0);
        summaryHeaderCell.setCellValue("Summary Statistics");
        
        String[][] summaryData = {
            {"Total Transactions", analytics.getTotalTransactions().toString()},
            {"Total Amount", analytics.getTotalAmount().toString()},
            {"Average Transaction", analytics.getAverageTransaction().toString()},
            {"Peak Hour", analytics.getPeakHour() + ":00"}
        };
        
        for (int i = 0; i < summaryData.length; i++) {
            Row row = sheet.createRow(4 + i);
            row.createCell(0).setCellValue(summaryData[i][0]);
            row.createCell(1).setCellValue(summaryData[i][1]);
        }
    }
}