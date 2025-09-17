package com.bmsp.bmsp.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LoanTypePerformanceDTO {
    private String loanType;
    private Long count;
    private BigDecimal totalAmount;
    private BigDecimal averageInterestRate;
}