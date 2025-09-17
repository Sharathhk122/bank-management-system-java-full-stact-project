// AccountServiceImpl.java
package com.bmsp.bmsp.service.account;

import com.bmsp.bmsp.dto.request.account.AccountCreateRequest;
import com.bmsp.bmsp.dto.response.account.AccountResponse;
import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.repository.account.AccountRepository;
import com.bmsp.bmsp.service.kyc.KYCService;
import com.bmsp.bmsp.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bmsp.bmsp.model.account.AccountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final KYCService kycService;

    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request, User user) {
        // Check if KYC is approved
        if (!kycService.isKYCApproved(user)) {
            throw new IllegalStateException("KYC verification is required to create an account");
        }

        // Generate account number
        String accountNumber = accountNumberGenerator.generateAccountNumber(request.getBranchCode());
        
        // Ensure account number is unique
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = accountNumberGenerator.generateAccountNumber(request.getBranchCode());
        }

        // Create new account
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialDeposit() != null ? 
                          request.getInitialDeposit() : BigDecimal.ZERO);
        account.setUser(user);
        account.setBranchCode(request.getBranchCode());
        account.setBranchName(request.getBranchName());
        account.setIfscCode(request.getIfscCode());
        account.setMinimumBalance(request.getMinimumBalance());
        account.setInterestRate(request.getInterestRate());
        account.setAllowOverdraft(request.getAllowOverdraft() != null && request.getAllowOverdraft());
        account.setOverdraftLimit(request.getOverdraftLimit());

        Account savedAccount = accountRepository.save(account);
        return mapToAccountResponse(savedAccount);
    }

    @Override
    public List<AccountResponse> getUserAccounts(User user) {
        return accountRepository.findByUser(user).stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse getAccountDetails(String accountNumber, User user) {
        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return mapToAccountResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber, User user) {
        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Account has balance and cannot be closed");
        }
        
        account.setStatus(AccountStatus.CLOSED);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus());
        response.setBalance(account.getBalance());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        response.setBranchCode(account.getBranchCode());
        response.setBranchName(account.getBranchName());
        response.setIfscCode(account.getIfscCode());
        response.setMinimumBalance(account.getMinimumBalance());
        response.setInterestRate(account.getInterestRate());
        response.setAllowOverdraft(account.isAllowOverdraft());
        response.setOverdraftLimit(account.getOverdraftLimit());
        return response;
    }
}