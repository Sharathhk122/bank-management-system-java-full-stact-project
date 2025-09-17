package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerGrowthResponse {
    private long totalCustomers;
    private long newCustomers;
    private long activeCustomers;
}