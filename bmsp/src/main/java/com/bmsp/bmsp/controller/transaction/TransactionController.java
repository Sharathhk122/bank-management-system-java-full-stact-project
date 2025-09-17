// TransactionController.java
package com.bmsp.bmsp.controller.transaction;

import com.bmsp.bmsp.dto.request.transaction.TransactionRequest;
import com.bmsp.bmsp.dto.request.transaction.TransferRequest;
import com.bmsp.bmsp.dto.response.transaction.TransactionResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.transaction.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        TransactionResponse response = transactionService.deposit(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        TransactionResponse response = transactionService.withdraw(request, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        TransactionResponse response = transactionService.transfer(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        List<TransactionResponse> transactions = 
            transactionService.getTransactionHistory(accountNumber, user);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/{accountNumber}/filter")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistoryBetweenDates(
            @PathVariable String accountNumber,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        List<TransactionResponse> transactions = 
            transactionService.getTransactionHistoryBetweenDates(
                accountNumber, startDate, endDate, user);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/{accountNumber}/paginated")
    public ResponseEntity<Page<TransactionResponse>> getPaginatedTransactionHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        Page<TransactionResponse> transactions = 
            transactionService.getPaginatedTransactionHistory(
                accountNumber, page, size, user);
        return ResponseEntity.ok(transactions);
    }
}