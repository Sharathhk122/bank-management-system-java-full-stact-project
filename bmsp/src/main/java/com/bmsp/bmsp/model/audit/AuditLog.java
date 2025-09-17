package com.bmsp.bmsp.model.audit;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String action;
    
    @Column(length = 1000)
    private String details;
    
    private String targetId;
    
    private String performedBy;
    
    @CreationTimestamp
    private LocalDateTime timestamp;
    
    @Column(length = 45)
    private String ipAddress;
    
    private String userAgent;
}