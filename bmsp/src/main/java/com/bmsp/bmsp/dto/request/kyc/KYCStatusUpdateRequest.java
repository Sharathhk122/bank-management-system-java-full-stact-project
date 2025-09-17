// KYCStatusUpdateRequest.java
package com.bmsp.bmsp.dto.request.kyc;

import com.bmsp.bmsp.model.kyc.KYCStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KYCStatusUpdateRequest {
    @NotNull
    private KYCStatus status;
    
    private String rejectionReason;
}