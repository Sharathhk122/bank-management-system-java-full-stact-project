package com.bmsp.bmsp.dto.request.kyc;

import com.bmsp.bmsp.model.kyc.KYCDocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class KYCDocumentUploadRequest {
    @NotNull
    private KYCDocumentType documentType;
    
    @NotBlank
    private String documentNumber;
    
    @NotNull
    private MultipartFile documentFrontImage;
    
    private MultipartFile documentBackImage;
    
    @NotNull
    private MultipartFile selfieImage;
}