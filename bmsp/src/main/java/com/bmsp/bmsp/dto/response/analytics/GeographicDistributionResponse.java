package com.bmsp.bmsp.dto.response.analytics;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class GeographicDistributionResponse {
    private String region;
    private long customerCount;
    private BigDecimal totalBalance;
}