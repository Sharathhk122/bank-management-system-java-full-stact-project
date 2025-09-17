package com.bmsp.bmsp.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyGrowthDTO {
    private Integer year;
    private Integer month;
    private Long count;
}