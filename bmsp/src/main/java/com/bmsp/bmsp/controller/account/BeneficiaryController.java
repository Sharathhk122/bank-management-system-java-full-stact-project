// BeneficiaryController.java
package com.bmsp.bmsp.controller.account;

import com.bmsp.bmsp.dto.request.account.BeneficiaryRequest;
import com.bmsp.bmsp.dto.response.account.BeneficiaryResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.account.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(
            @Valid @RequestBody BeneficiaryRequest request,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        BeneficiaryResponse response = beneficiaryService.addBeneficiary(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getUserBeneficiaries(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        List<BeneficiaryResponse> beneficiaries = beneficiaryService.getUserBeneficiaries(user);
        return ResponseEntity.ok(beneficiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiaryDetails(
            @PathVariable Long id,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        BeneficiaryResponse beneficiary = beneficiaryService.getBeneficiaryDetails(id, user);
        return ResponseEntity.ok(beneficiary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long id,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        beneficiaryService.deleteBeneficiary(id, user);
        return ResponseEntity.noContent().build();
    }
}