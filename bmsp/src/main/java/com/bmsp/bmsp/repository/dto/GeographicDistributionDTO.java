package com.bmsp.bmsp.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GeographicDistributionDTO {
    private String city;
    private String state;
    private Long userCount;
    private BigDecimal totalBalance;
}