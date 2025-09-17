package com.bmsp.bmsp.dto.response.loan;

import com.bmsp.bmsp.model.loan.EMIRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EMIScheduleResponse {
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal remainingPrincipal;
    private EMIRecord.EMIStatus status;
    private LocalDate paymentDate;
}