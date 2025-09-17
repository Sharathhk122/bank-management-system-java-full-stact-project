package com.bmsp.bmsp.service.kyc;

import com.bmsp.bmsp.dto.request.kyc.KYCDocumentUploadRequest;
import com.bmsp.bmsp.dto.request.kyc.KYCStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.kyc.KYCResponse;
import com.bmsp.bmsp.dto.response.kyc.KYCSummaryResponse;
import com.bmsp.bmsp.exception.ResourceNotFoundException;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.kyc.KYC;
import com.bmsp.bmsp.model.kyc.KYCStatus;
import com.bmsp.bmsp.repository.kyc.KYCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KYCServiceImpl implements KYCService {

    private final KYCRepository kycRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public KYCResponse submitKYC(User user, KYCDocumentUploadRequest request) throws IOException {
        try {
            // Validate request
            if (request == null || request.getDocumentFrontImage() == null || request.getSelfieImage() == null) {
                throw new IllegalArgumentException("Required documents are missing");
            }

            // Check if KYC already exists for this user
            KYC existingKYC = kycRepository.findByUser(user).orElse(null);
            
            // If KYC exists and is approved, don't allow resubmission
            if (existingKYC != null && existingKYC.getStatus() == KYCStatus.APPROVED) {
                throw new IllegalStateException("KYC is already approved and cannot be resubmitted");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            KYC kyc;
            if (existingKYC != null) {
                // Update existing KYC record for resubmission
                kyc = existingKYC;
                kyc.setStatus(KYCStatus.PENDING);
                kyc.setRejectionReason(null);
                kyc.setVerifiedAt(null);
                kyc.setVerifiedBy(null);
            } else {
                // Create new KYC record
                kyc = new KYC();
                kyc.setUser(user);
            }

            kyc.setDocumentType(request.getDocumentType());
            kyc.setDocumentNumber(request.getDocumentNumber());
            kyc.setSubmittedAt(LocalDateTime.now());

            // Save files and set URLs
            kyc.setDocumentFrontImageUrl(saveAndGetFileUrl(request.getDocumentFrontImage()));
            if (request.getDocumentBackImage() != null && !request.getDocumentBackImage().isEmpty()) {
                kyc.setDocumentBackImageUrl(saveAndGetFileUrl(request.getDocumentBackImage()));
            }
            kyc.setSelfieImageUrl(saveAndGetFileUrl(request.getSelfieImage()));

            KYC savedKYC = kycRepository.save(kyc);
            return mapToKYCResponse(savedKYC);
        } catch (Exception e) {
            log.error("Error submitting KYC for user {}: {}", user.getId(), e.getMessage());
            throw e;
        }
    }

    private String saveAndGetFileUrl(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    @Override
    public KYCResponse getKYCStatus(User user) {
        KYC kyc = kycRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found"));
        return mapToKYCResponse(kyc);
    }

    @Override
    @Transactional
    public KYCResponse updateKYCStatus(Long kycId, KYCStatusUpdateRequest request, String verifiedBy) {
        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found"));

        if (request.getStatus() == KYCStatus.REJECTED && 
            (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new IllegalArgumentException("Rejection reason is required when rejecting KYC");
        }

        kyc.setStatus(request.getStatus());
        kyc.setVerifiedAt(LocalDateTime.now());
        kyc.setVerifiedBy(verifiedBy);

        if (request.getStatus() == KYCStatus.REJECTED) {
            kyc.setRejectionReason(request.getRejectionReason());
        } else {
            kyc.setRejectionReason(null); // Clear rejection reason if not rejected
        }

        KYC updatedKYC = kycRepository.save(kyc);
        return mapToKYCResponse(updatedKYC);
    }

    @Override
    public boolean isKYCApproved(User user) {
        return kycRepository.findByUser(user)
                .map(kyc -> kyc.getStatus() == KYCStatus.APPROVED)
                .orElse(false);
    }

    @Override
    public List<KYCSummaryResponse> getAllKYCSubmissions() {
        List<KYC> allKYC = kycRepository.findAll();
        return allKYC.stream()
                .map(this::mapToKYCSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<KYCSummaryResponse> getKYCSubmissionsByStatus(KYCStatus status) {
        List<KYC> kycList = kycRepository.findByStatus(status);
        return kycList.stream()
                .map(this::mapToKYCSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public KYCResponse getKYCById(Long kycId) {
        KYC kyc = kycRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC details not found"));
        return mapToKYCResponse(kyc);
    }

    private KYCResponse mapToKYCResponse(KYC kyc) {
        KYCResponse response = new KYCResponse();
        response.setId(kyc.getId());
        response.setStatus(kyc.getStatus());
        response.setDocumentType(kyc.getDocumentType());
        response.setDocumentNumber(kyc.getDocumentNumber());
        response.setDocumentFrontImageUrl(kyc.getDocumentFrontImageUrl());
        response.setDocumentBackImageUrl(kyc.getDocumentBackImageUrl());
        response.setSelfieImageUrl(kyc.getSelfieImageUrl());
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        response.setVerifiedBy(kyc.getVerifiedBy());
        response.setRejectionReason(kyc.getRejectionReason());
        return response;
    }

    private KYCSummaryResponse mapToKYCSummaryResponse(KYC kyc) {
        KYCSummaryResponse response = new KYCSummaryResponse();
        response.setId(kyc.getId());
        response.setUserId(kyc.getUser().getId());
        response.setUserEmail(kyc.getUser().getEmail());
        
        // Use getFullName() instead of getFirstName() + getLastName()
        response.setUserName(kyc.getUser().getFullName());
        
        response.setStatus(kyc.getStatus());
        response.setDocumentType(kyc.getDocumentType());
        response.setDocumentNumber(kyc.getDocumentNumber());
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        response.setVerifiedBy(kyc.getVerifiedBy());
        response.setRejectionReason(kyc.getRejectionReason());
        return response;
    }

    @Override
    public boolean hasApprovedKYC(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasApprovedKYC'");
    }
}