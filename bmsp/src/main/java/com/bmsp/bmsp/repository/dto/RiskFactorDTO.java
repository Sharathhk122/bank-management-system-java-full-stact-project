package com.bmsp.bmsp.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RiskFactorDTO {
    private String accountType;
    private Long count;
    private BigDecimal totalBalance;
}