package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private String action;
    private String details;
    private String targetId;
    private LocalDateTime timestamp;
}