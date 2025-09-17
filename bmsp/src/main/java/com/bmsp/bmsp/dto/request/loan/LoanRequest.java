package com.bmsp.bmsp.dto.request.loan;

import com.bmsp.bmsp.model.loan.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequest {
    @NotNull(message = "Loan type is required")
    private LoanType loanType;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "1000.0", message = "Loan amount must be at least 1000")
    private BigDecimal loanAmount;
    
    @NotNull(message = "Tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 month")
    private Integer tenureMonths;
    
    @NotNull(message = "Account number is required")
    private String accountNumber;
}