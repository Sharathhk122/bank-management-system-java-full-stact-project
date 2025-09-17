// AccountController.java
package com.bmsp.bmsp.controller.account;

import com.bmsp.bmsp.dto.request.account.AccountCreateRequest;
import com.bmsp.bmsp.dto.response.account.AccountResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.account.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        AccountResponse response = accountService.createAccount(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getUserAccounts(Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        List<AccountResponse> accounts = accountService.getUserAccounts(user);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountDetails(
            @PathVariable String accountNumber,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        AccountResponse account = accountService.getAccountDetails(accountNumber, user);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        accountService.deleteAccount(accountNumber, user);
        return ResponseEntity.noContent().build();
    }
}