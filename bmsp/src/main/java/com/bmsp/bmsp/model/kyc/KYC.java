// KYC.java
package com.bmsp.bmsp.model.kyc;

import com.bmsp.bmsp.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
public class KYC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private KYCStatus status = KYCStatus.PENDING;

    @Column(nullable = false)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCDocumentType documentType;

    private String documentFrontImageUrl;
    private String documentBackImageUrl;
    private String selfieImageUrl;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private String rejectionReason;
}