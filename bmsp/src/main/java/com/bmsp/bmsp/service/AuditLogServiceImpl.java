package com.bmsp.bmsp.service;

import com.bmsp.bmsp.dto.response.admin.AuditLogResponse;
import com.bmsp.bmsp.repository.audit.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public Page<AuditLogResponse> getAuditLogs(int page, int size, String action, LocalDate date) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        
        LocalDateTime startDateTime = date != null ? date.atStartOfDay() : null;
        LocalDateTime endDateTime = date != null ? date.atTime(23, 59, 59) : null;
        
        Page<com.bmsp.bmsp.model.audit.AuditLog> auditLogs;
        if (action != null && !action.isEmpty() && date != null) {
            auditLogs = auditLogRepository.findByActionAndTimestampBetween(action, startDateTime, endDateTime, pageable);
        } else if (action != null && !action.isEmpty()) {
            auditLogs = auditLogRepository.findByAction(action, pageable);
        } else if (date != null) {
            auditLogs = auditLogRepository.findByTimestampBetween(startDateTime, endDateTime, pageable);
        } else {
            auditLogs = auditLogRepository.findAll(pageable);
        }
        
        return auditLogs.map(auditLog -> AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .targetId(auditLog.getTargetId())
                .timestamp(auditLog.getTimestamp())
                .build());
    }
}