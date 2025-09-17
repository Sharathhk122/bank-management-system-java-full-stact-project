// BeneficiaryRequest.java
package com.bmsp.bmsp.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeneficiaryRequest {
    @NotBlank
    private String accountNumber;

    @NotBlank
    private String accountHolderName;

    @NotBlank
    private String bankName;

    private String branchName;
    private String ifscCode;

    @NotBlank
    private String nickname;
}