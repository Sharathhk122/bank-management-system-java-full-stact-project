package com.bmsp.bmsp.service.kyc;

import com.bmsp.bmsp.dto.request.kyc.KYCDocumentUploadRequest;
import com.bmsp.bmsp.dto.request.kyc.KYCStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.kyc.KYCResponse;
import com.bmsp.bmsp.dto.response.kyc.KYCSummaryResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.kyc.KYCStatus;

import java.io.IOException;
import java.util.List;

public interface KYCService {
    KYCResponse submitKYC(User user, KYCDocumentUploadRequest request) throws IOException;
    KYCResponse getKYCStatus(User user);
    KYCResponse updateKYCStatus(Long kycId, KYCStatusUpdateRequest request, String verifiedBy);
    boolean isKYCApproved(User user);
    
    // Check if user has approved KYC
    boolean hasApprovedKYC(User user);
    
    // Admin methods
    List<KYCSummaryResponse> getAllKYCSubmissions();
    List<KYCSummaryResponse> getKYCSubmissionsByStatus(KYCStatus status);
    KYCResponse getKYCById(Long kycId);
}