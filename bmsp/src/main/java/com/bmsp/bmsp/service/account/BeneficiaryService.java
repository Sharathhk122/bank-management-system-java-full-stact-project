// BeneficiaryService.java
package com.bmsp.bmsp.service.account;

import com.bmsp.bmsp.dto.request.account.BeneficiaryRequest;
import com.bmsp.bmsp.dto.response.account.BeneficiaryResponse;
import com.bmsp.bmsp.model.auth.User;

import java.util.List;

public interface BeneficiaryService {
    BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, User user);
    List<BeneficiaryResponse> getUserBeneficiaries(User user);
    BeneficiaryResponse getBeneficiaryDetails(Long id, User user);
    void deleteBeneficiary(Long id, User user);
}