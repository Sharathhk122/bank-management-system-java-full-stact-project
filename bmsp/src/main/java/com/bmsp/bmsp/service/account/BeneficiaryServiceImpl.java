// BeneficiaryServiceImpl.java
package com.bmsp.bmsp.service.account;

import com.bmsp.bmsp.dto.request.account.BeneficiaryRequest;
import com.bmsp.bmsp.dto.response.account.BeneficiaryResponse;
import com.bmsp.bmsp.model.account.Beneficiary;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.repository.account.BeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    @Override
    @Transactional
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest request, User user) {
        // Check if beneficiary already exists
        if (beneficiaryRepository.existsByAccountNumberAndUser(request.getAccountNumber(), user)) {
            throw new RuntimeException("Beneficiary already exists");
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setAccountHolderName(request.getAccountHolderName());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setBranchName(request.getBranchName());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setNickname(request.getNickname());
        beneficiary.setUser(user);

        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);
        return mapToBeneficiaryResponse(savedBeneficiary);
    }

    @Override
    public List<BeneficiaryResponse> getUserBeneficiaries(User user) {
        return beneficiaryRepository.findByUser(user).stream()
                .map(this::mapToBeneficiaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BeneficiaryResponse getBeneficiaryDetails(Long id, User user) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));
        return mapToBeneficiaryResponse(beneficiary);
    }

    @Override
    @Transactional
    public void deleteBeneficiary(Long id, User user) {
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Beneficiary not found"));
        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponse mapToBeneficiaryResponse(Beneficiary beneficiary) {
        BeneficiaryResponse response = new BeneficiaryResponse();
        response.setId(beneficiary.getId());
        response.setAccountNumber(beneficiary.getAccountNumber());
        response.setAccountHolderName(beneficiary.getAccountHolderName());
        response.setBankName(beneficiary.getBankName());
        response.setBranchName(beneficiary.getBranchName());
        response.setIfscCode(beneficiary.getIfscCode());
        response.setNickname(beneficiary.getNickname());
        return response;
    }
}