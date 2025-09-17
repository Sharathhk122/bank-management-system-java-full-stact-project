// BeneficiaryResponse.java
package com.bmsp.bmsp.dto.response.account;

import lombok.Data;

@Data
public class BeneficiaryResponse {
    private Long id;
    private String accountNumber;
    private String accountHolderName;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String nickname;
}