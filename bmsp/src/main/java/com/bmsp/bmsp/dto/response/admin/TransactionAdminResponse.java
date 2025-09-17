package com.bmsp.bmsp.dto.response.admin;

import com.bmsp.bmsp.model.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionAdminResponse {
    private Long id;
    private String accountNumber;
    private TransactionType type;
    private BigDecimal amount;
    private String referenceNumber;
    private String description;
    private LocalDateTime transactionDate;
    private BigDecimal balanceAfter;
}