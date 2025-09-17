package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransactionAnalyticsResponse {
    private Long totalTransactions;
    private BigDecimal totalAmount;
    private BigDecimal averageTransaction;
    private int peakHour;
}