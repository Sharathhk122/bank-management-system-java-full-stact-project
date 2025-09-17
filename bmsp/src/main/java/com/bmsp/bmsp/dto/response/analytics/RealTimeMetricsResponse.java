package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RealTimeMetricsResponse {
    private int activeSessions;
    private BigDecimal transactionsLastHour;
    private int newUsersLastHour;
    private BigDecimal systemUptime;
    private int responseTimeMs;
}