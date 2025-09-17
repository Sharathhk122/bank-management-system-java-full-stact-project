package com.bmsp.bmsp.dto.request.admin;

import lombok.Data;
import java.util.List;

@Data
public class BulkAccountStatusUpdateRequest {
    private List<String> accountNumbers;
    private String status;
}