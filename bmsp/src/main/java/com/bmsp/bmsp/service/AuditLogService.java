package com.bmsp.bmsp.service;

import com.bmsp.bmsp.dto.response.admin.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface AuditLogService {
    void logAdminAction(String action, String details, String targetId);
    Page<AuditLogResponse> getAuditLogs(Pageable pageable, String action, LocalDate date);
}