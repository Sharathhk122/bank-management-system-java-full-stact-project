package com.bmsp.bmsp.service.transaction;

import com.bmsp.bmsp.dto.request.transaction.TransactionRequest;
import com.bmsp.bmsp.dto.request.transaction.TransferRequest;
import com.bmsp.bmsp.dto.response.transaction.TransactionResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.transaction.TransactionType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    TransactionResponse deposit(TransactionRequest request, User user);
    TransactionResponse withdraw(TransactionRequest request, User user);
    TransactionResponse transfer(TransferRequest request, User user);
    TransactionResponse processAdminTransaction(TransactionRequest request, TransactionType type);
    List<TransactionResponse> getTransactionHistory(String accountNumber, User user);
    List<TransactionResponse> getTransactionHistoryBetweenDates(
            String accountNumber, LocalDate startDate, LocalDate endDate, User user);
    Page<TransactionResponse> getPaginatedTransactionHistory(
            String accountNumber, int page, int size, User user);
}