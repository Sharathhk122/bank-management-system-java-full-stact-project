package com.bmsp.bmsp.dto.response.admin;

import com.bmsp.bmsp.model.loan.LoanStatus;
import com.bmsp.bmsp.model.loan.LoanType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LoanSummary {
    private Long id;
    private String loanAccountNumber;
    private LoanType loanType;
    private BigDecimal loanAmount;
    private LoanStatus status;
    private LocalDateTime createdAt;
}