package com.bmsp.bmsp.dto.request.admin;

import com.bmsp.bmsp.model.auth.UserStatus;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {
    private UserStatus status;
    private String reason;
}