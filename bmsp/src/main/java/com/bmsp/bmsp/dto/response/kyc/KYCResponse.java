package com.bmsp.bmsp.dto.response.kyc;

import com.bmsp.bmsp.model.kyc.KYCStatus;
import com.bmsp.bmsp.model.kyc.KYCDocumentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KYCResponse {
    private Long id;
    private KYCStatus status;
    private String documentNumber;
    private KYCDocumentType documentType;
    private String documentFrontImageUrl;
    private String documentBackImageUrl;
    private String selfieImageUrl;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private String rejectionReason;
}