package com.bmsp.bmsp.dto.response.admin;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BroadcastResponse {
    private String message;
    private int recipientsCount;
    private LocalDateTime timestamp;
}