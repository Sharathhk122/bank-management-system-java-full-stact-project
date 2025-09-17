package com.bmsp.bmsp.service.report;

import com.bmsp.bmsp.dto.response.admin.TransactionAnalyticsResponse;
import com.bmsp.bmsp.model.loan.Loan;
import com.bmsp.bmsp.model.transaction.Transaction;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.service.admin.AdminService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDFGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PDFGeneratorService.class);
    private final AdminService adminService;

    public byte[] generateTransactionReport(LocalDate startDate, LocalDate endDate, String transactionType, List<Transaction> transactions) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate()); // Landscape for better table view
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Transaction Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date range
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String dateRange = "Period: " + startDate.format(DateTimeFormatter.ISO_DATE) + 
                             " to " + endDate.format(DateTimeFormatter.ISO_DATE);
            Paragraph period = new Paragraph(dateRange, subTitleFont);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Add transaction type filter info
            if (transactionType != null && !transactionType.isEmpty()) {
                Paragraph typeInfo = new Paragraph("Transaction Type: " + transactionType, subTitleFont);
                typeInfo.setAlignment(Element.ALIGN_CENTER);
                typeInfo.setSpacingAfter(20);
                document.add(typeInfo);
            }

            // Add summary statistics if transactions are available
            try {
                if (transactions != null && !transactions.isEmpty()) {
                    TransactionAnalyticsResponse analytics = adminService.getTransactionAnalytics(startDate, endDate);
                    if (analytics != null) {
                        addSummarySection(document, analytics);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to generate analytics section: {}", e.getMessage());
                // Continue without analytics section
            }

            // Add transaction table
            addTransactionTable(document, transactions);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to generate PDF report", e);
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    private void addSummarySection(Document document, TransactionAnalyticsResponse analytics) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph summaryHeader = new Paragraph("Summary Statistics", headerFont);
        summaryHeader.setSpacingAfter(10);
        document.add(summaryHeader);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10);
        summaryTable.setSpacingAfter(20);

        addSummaryRow(summaryTable, "Total Transactions", 
            analytics.getTotalTransactions() != null ? analytics.getTotalTransactions().toString() : "0");
        addSummaryRow(summaryTable, "Total Amount", 
            analytics.getTotalAmount() != null ? analytics.getTotalAmount().toString() : "₹0.00");
        addSummaryRow(summaryTable, "Average Transaction", 
            analytics.getAverageTransaction() != null ? analytics.getAverageTransaction().toString() : "₹0.00");
        addSummaryRow(summaryTable, "Peak Hour", 
            analytics.getPeakHour() != 0 ? analytics.getPeakHour() + ":00" : "N/A");

        document.add(summaryTable);
    }

    private void addSummaryRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTransactionTable(Document document, List<Transaction> transactions) throws DocumentException {
        if (transactions == null || transactions.isEmpty()) {
            Font noDataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph noData = new Paragraph("No transactions found for the selected criteria.", noDataFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingBefore(20);
            document.add(noData);
            return;
        }

        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Set column widths
        float[] columnWidths = {1.5f, 1f, 2f, 1.5f, 2f, 3f};
        table.setWidths(columnWidths);

        // Table headers
        addTableHeaderCell(table, "Date", tableHeaderFont);
        addTableHeaderCell(table, "Type", tableHeaderFont);
        addTableHeaderCell(table, "Account", tableHeaderFont);
        addTableHeaderCell(table, "Amount", tableHeaderFont);
        addTableHeaderCell(table, "Reference", tableHeaderFont);
        addTableHeaderCell(table, "Description", tableHeaderFont);

        // Add transaction data
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (Transaction transaction : transactions) {
            addTableCell(table, transaction.getTransactionDate() != null ? 
                transaction.getTransactionDate().format(DateTimeFormatter.ISO_DATE) : "N/A", dataFont);
            addTableCell(table, transaction.getType() != null ? 
                transaction.getType().name() : "N/A", dataFont);
            addTableCell(table, transaction.getAccount() != null && transaction.getAccount().getAccountNumber() != null ? 
                transaction.getAccount().getAccountNumber() : "N/A", dataFont);
            addTableCell(table, transaction.getAmount() != null ? 
                "₹" + transaction.getAmount().toString() : "₹0.00", dataFont);
            addTableCell(table, transaction.getReferenceNumber() != null ? 
                transaction.getReferenceNumber() : "N/A", dataFont);
            addTableCell(table, transaction.getDescription() != null ? 
                transaction.getDescription() : "N/A", dataFont);
        }

        document.add(table);
    }

    private void addTableHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    public byte[] generateLoanReport(LocalDate startDate, LocalDate endDate, String loanStatus, List<Loan> loans) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Loan Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date range
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String dateRange = "Period: " + startDate.format(DateTimeFormatter.ISO_DATE) + 
                             " to " + endDate.format(DateTimeFormatter.ISO_DATE);
            Paragraph period = new Paragraph(dateRange, subTitleFont);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Add loan status filter info
            if (loanStatus != null && !loanStatus.isEmpty()) {
                Paragraph statusInfo = new Paragraph("Loan Status: " + loanStatus, subTitleFont);
                statusInfo.setAlignment(Element.ALIGN_CENTER);
                statusInfo.setSpacingAfter(20);
                document.add(statusInfo);
            }

            // Add loan summary table
            addLoanSummaryTable(document, loans);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to generate loan report", e);
            throw new RuntimeException("Failed to generate loan report: " + e.getMessage(), e);
        }
    }

    private void addLoanSummaryTable(Document document, List<Loan> loans) throws DocumentException {
        if (loans == null || loans.isEmpty()) {
            Font noDataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph noData = new Paragraph("No loans found for the selected criteria.", noDataFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingBefore(20);
            document.add(noData);
            return;
        }

        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Set column widths
        float[] columnWidths = {1.5f, 2f, 1.5f, 1.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Table headers
        addTableHeaderCell(table, "Loan ID", tableHeaderFont);
        addTableHeaderCell(table, "Customer", tableHeaderFont);
        addTableHeaderCell(table, "Loan Type", tableHeaderFont);
        addTableHeaderCell(table, "Amount", tableHeaderFont);
        addTableHeaderCell(table, "Status", tableHeaderFont);
        addTableHeaderCell(table, "Disbursed Date", tableHeaderFont);

        // Add loan data
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (Loan loan : loans) {
            addTableCell(table, loan.getLoanAccountNumber() != null ? loan.getLoanAccountNumber() : "N/A", dataFont);
            addTableCell(table, loan.getUser() != null ? loan.getUser().getFullName() : "N/A", dataFont);
            addTableCell(table, loan.getLoanType() != null ? loan.getLoanType().name() : "N/A", dataFont);
            addTableCell(table, loan.getLoanAmount() != null ? "₹" + loan.getLoanAmount().toString() : "₹0.00", dataFont);
            addTableCell(table, loan.getStatus() != null ? loan.getStatus().name() : "N/A", dataFont);
            addTableCell(table, loan.getCreatedAt() != null ? 
                loan.getCreatedAt().format(DateTimeFormatter.ISO_DATE) : "N/A", dataFont);
        }

        document.add(table);
    }

    public byte[] generateCustomerReport(LocalDate startDate, LocalDate endDate, List<User> customers) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Customer Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add date range
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String dateRange = "Period: " + startDate.format(DateTimeFormatter.ISO_DATE) + 
                             " to " + endDate.format(DateTimeFormatter.ISO_DATE);
            Paragraph period = new Paragraph(dateRange, subTitleFont);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Add customer summary table
            addCustomerSummaryTable(document, customers);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to generate customer report", e);
            throw new RuntimeException("Failed to generate customer report: " + e.getMessage(), e);
        }
    }

    private void addCustomerSummaryTable(Document document, List<User> customers) throws DocumentException {
        if (customers == null || customers.isEmpty()) {
            Font noDataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph noData = new Paragraph("No customers found for the selected criteria.", noDataFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingBefore(20);
            document.add(noData);
            return;
        }

        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        // Set column widths
        float[] columnWidths = {1.5f, 2f, 2.5f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        // Table headers
        addTableHeaderCell(table, "Customer ID", tableHeaderFont);
        addTableHeaderCell(table, "Name", tableHeaderFont);
        addTableHeaderCell(table, "Email", tableHeaderFont);
        addTableHeaderCell(table, "Phone", tableHeaderFont);
        addTableHeaderCell(table, "Join Date", tableHeaderFont);

        // Add customer data
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        
        for (User customer : customers) {
            addTableCell(table, customer.getId() != null ? customer.getId().toString() : "N/A", dataFont);
            addTableCell(table, customer.getFullName() != null ? customer.getFullName() : "N/A", dataFont);
            addTableCell(table, customer.getEmail() != null ? customer.getEmail() : "N/A", dataFont);
            addTableCell(table, customer.getPhone() != null ? customer.getPhone() : "N/A", dataFont);
            addTableCell(table, customer.getCreatedAt() != null ? 
                customer.getCreatedAt().format(DateTimeFormatter.ISO_DATE) : "N/A", dataFont);
        }

        document.add(table);
    }

    public byte[] generateFinancialSummaryReport(int year, int month, 
            long totalCustomers, long totalTransactions, BigDecimal totalTransactionAmount,
            long totalLoans, BigDecimal totalLoanAmount) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Financial Summary Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add period
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String periodText = "Period: " + year + "-" + String.format("%02d", month);
            Paragraph period = new Paragraph(periodText, subTitleFont);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(20);
            document.add(period);

            // Add financial summary
            addFinancialSummary(document, totalCustomers, totalTransactions, 
                    totalTransactionAmount, totalLoans, totalLoanAmount);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to generate financial summary report", e);
            throw new RuntimeException("Failed to generate financial summary report: " + e.getMessage(), e);
        }
    }

    private void addFinancialSummary(Document document, long totalCustomers, long totalTransactions, 
            BigDecimal totalTransactionAmount, long totalLoans, BigDecimal totalLoanAmount) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Paragraph summaryHeader = new Paragraph("Financial Summary", headerFont);
        summaryHeader.setSpacingAfter(10);
        document.add(summaryHeader);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10);
        summaryTable.setSpacingAfter(20);

        addSummaryRow(summaryTable, "Total New Customers", String.valueOf(totalCustomers));
        addSummaryRow(summaryTable, "Total Transactions", String.valueOf(totalTransactions));
        addSummaryRow(summaryTable, "Total Transaction Amount", "₹" + totalTransactionAmount);
        addSummaryRow(summaryTable, "Total New Loans", String.valueOf(totalLoans));
        addSummaryRow(summaryTable, "Total Loan Portfolio", "₹" + totalLoanAmount);

        document.add(summaryTable);
    }
}