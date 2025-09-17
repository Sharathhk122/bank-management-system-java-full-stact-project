package com.bmsp.bmsp.service.transaction;

import com.bmsp.bmsp.dto.request.transaction.TransactionRequest;
import com.bmsp.bmsp.dto.request.transaction.TransferRequest;
import com.bmsp.bmsp.dto.response.transaction.TransactionResponse;
import com.bmsp.bmsp.exception.InsufficientBalanceException;
import com.bmsp.bmsp.exception.TransactionException;
import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.account.AccountStatus;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.transaction.Transaction;
import com.bmsp.bmsp.model.transaction.TransactionType;
import com.bmsp.bmsp.repository.account.AccountRepository;
import com.bmsp.bmsp.repository.transaction.TransactionRepository;
import com.bmsp.bmsp.util.ReferenceNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ReferenceNumberGenerator referenceNumberGenerator;

    @Override
    @Transactional
    public TransactionResponse deposit(TransactionRequest request, User user) {
        Account account = validateAccountAccess(request.getAccountNumber(), user);
        
        // Process deposit
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(referenceNumberGenerator.generateReferenceNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request, User user) {
        Account account = validateAccountAccess(request.getAccountNumber(), user);
        
        // Validate sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }
        
        // Process withdrawal
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(referenceNumberGenerator.generateReferenceNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse processAdminTransaction(TransactionRequest request, TransactionType type) {
        Account account = accountRepository.findById(request.getAccountNumber())
                .orElseThrow(() -> new TransactionException("Account not found"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionException("Account is not active");
        }
        
        BigDecimal newBalance;
        
        if (type == TransactionType.DEPOSIT) {
            newBalance = account.getBalance().add(request.getAmount());
        } else if (type == TransactionType.WITHDRAWAL) {
            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for withdrawal");
            }
            newBalance = account.getBalance().subtract(request.getAmount());
        } else {
            throw new TransactionException("Invalid transaction type for admin operation");
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());
        transaction.setReferenceNumber(referenceNumberGenerator.generateReferenceNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request, User user) {
        // Validate source account access
        Account fromAccount = validateAccountAccess(request.getFromAccountNumber(), user);
        
        // Prevent transferring to same account
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new TransactionException("Cannot transfer to the same account");
        }
        
        // Validate sufficient balance
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }
        
        // Find destination account using primary key lookup
        Account toAccount = accountRepository.findById(request.getToAccountNumber())
                .orElseThrow(() -> new TransactionException("Destination account not found"));
        
        // Check destination account status
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionException("Destination account is not active");
        }

        // Process transfer
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        
        fromAccount.setBalance(fromNewBalance);
        toAccount.setBalance(toNewBalance);
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // Generate a single reference number for both transactions
        String referenceNumber = referenceNumberGenerator.generateReferenceNumber();
        
        // Create outgoing transaction
        Transaction outgoing = new Transaction();
        outgoing.setAccount(fromAccount);
        outgoing.setType(TransactionType.TRANSFER_OUT);
        outgoing.setAmount(request.getAmount());
        outgoing.setBalanceAfter(fromNewBalance);
        outgoing.setDescription(request.getDescription());
        outgoing.setReferenceNumber(referenceNumber);
        outgoing.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(outgoing);
        
        // Create incoming transaction
        Transaction incoming = new Transaction();
        incoming.setAccount(toAccount);
        incoming.setType(TransactionType.TRANSFER_IN);
        incoming.setAmount(request.getAmount());
        incoming.setBalanceAfter(toNewBalance);
        incoming.setDescription(request.getDescription());
        incoming.setReferenceNumber(referenceNumber);
        incoming.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(incoming);
        
        return mapToTransactionResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(String accountNumber, User user) {
        Account account = validateAccountAccess(accountNumber, user);
        return transactionRepository.findByAccountOrderByTransactionDateDesc(account)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionHistoryBetweenDates(
            String accountNumber, LocalDate startDate, LocalDate endDate, User user) {
        Account account = validateAccountAccess(accountNumber, user);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository
                .findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(
                        account, startDateTime, endDateTime)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionResponse> getPaginatedTransactionHistory(
            String accountNumber, int page, int size, User user) {
        Account account = validateAccountAccess(accountNumber, user);
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository
                .findByAccountOrderByTransactionDateDesc(account, pageable)
                .map(this::mapToTransactionResponse);
    }

    private Account validateAccountAccess(String accountNumber, User user) {
        // For admin transactions, skip user validation
        if (user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"))) {
            return accountRepository.findById(accountNumber)
                    .orElseThrow(() -> new TransactionException("Account not found"));
        }
        
        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new TransactionException("Account not found or access denied"));
        
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionException("Account is not active");
        }
        return account;
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAccountNumber(transaction.getAccount().getAccountNumber());
        response.setType(transaction.getType());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setReferenceNumber(transaction.getReferenceNumber());
        return response;
    }
}