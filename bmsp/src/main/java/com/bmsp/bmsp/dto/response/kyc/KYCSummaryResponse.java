// KYCSummaryResponse.java
package com.bmsp.bmsp.dto.response.kyc;

import com.bmsp.bmsp.model.kyc.KYCStatus;
import com.bmsp.bmsp.model.kyc.KYCDocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KYCSummaryResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userName;
    private KYCStatus status;
    private String documentNumber;
    private KYCDocumentType documentType;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private String rejectionReason;
}