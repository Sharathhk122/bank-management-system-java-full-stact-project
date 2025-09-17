package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkOperationResponse {
    private int totalProcessed;
    private int successCount;
    private int failureCount;
}