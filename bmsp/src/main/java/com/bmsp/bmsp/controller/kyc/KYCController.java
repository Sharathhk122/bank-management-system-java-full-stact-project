package com.bmsp.bmsp.controller.kyc;

import com.bmsp.bmsp.dto.request.kyc.KYCDocumentUploadRequest;
import com.bmsp.bmsp.dto.request.kyc.KYCStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.kyc.KYCResponse;
import com.bmsp.bmsp.dto.response.kyc.KYCSummaryResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.kyc.KYCDocumentType;
import com.bmsp.bmsp.model.kyc.KYCStatus;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.kyc.KYCService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class KYCController {

    private final KYCService kycService;

    public KYCController(KYCService kycService) {
        this.kycService = kycService;
    }

    @PostMapping(value = "/submit", consumes = "multipart/form-data")
    public ResponseEntity<KYCResponse> submitKYC(
            @RequestParam("documentType") String documentType,
            @RequestParam("documentNumber") String documentNumber,
            @RequestParam("documentFrontImage") MultipartFile documentFrontImage,
            @RequestParam(value = "documentBackImage", required = false) MultipartFile documentBackImage,
            @RequestParam("selfieImage") MultipartFile selfieImage,
            Authentication authentication) throws IOException {
        
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        
        KYCDocumentUploadRequest request = new KYCDocumentUploadRequest();
        request.setDocumentType(KYCDocumentType.valueOf(documentType));
        request.setDocumentNumber(documentNumber);
        request.setDocumentFrontImage(documentFrontImage);
        request.setDocumentBackImage(documentBackImage);
        request.setSelfieImage(selfieImage);

        KYCResponse response = kycService.submitKYC(user, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<KYCResponse> getKYCStatus(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        KYCResponse response = kycService.getKYCStatus(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{kycId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KYCResponse> updateKYCStatus(
            @PathVariable Long kycId,
            @RequestBody KYCStatusUpdateRequest request,
            Authentication authentication) {
        String verifiedBy = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        KYCResponse response = kycService.updateKYCStatus(kycId, request, verifiedBy);
        return ResponseEntity.ok(response);
    }

    // Admin endpoints - Get all KYC submissions
    @GetMapping("/admin/submissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KYCSummaryResponse>> getAllKYCSubmissions() {
        List<KYCSummaryResponse> submissions = kycService.getAllKYCSubmissions();
        return ResponseEntity.ok(submissions);
    }

    // Admin endpoints - Get KYC submissions by status
    @GetMapping("/admin/submissions/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KYCSummaryResponse>> getKYCSubmissionsByStatus(
            @PathVariable KYCStatus status) {
        List<KYCSummaryResponse> submissions = kycService.getKYCSubmissionsByStatus(status);
        return ResponseEntity.ok(submissions);
    }

    // Admin endpoints - Get specific KYC by ID
    @GetMapping("/admin/{kycId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KYCResponse> getKYCById(@PathVariable Long kycId) {
        KYCResponse response = kycService.getKYCById(kycId);
        return ResponseEntity.ok(response);
    }
}