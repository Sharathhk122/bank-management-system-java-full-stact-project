package com.bmsp.bmsp.service.admin;

import com.bmsp.bmsp.dto.request.admin.UserStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.admin.*;
import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.account.AccountStatus;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.auth.UserStatus;

import com.bmsp.bmsp.model.loan.LoanStatus;
import com.bmsp.bmsp.model.transaction.Transaction;
import com.bmsp.bmsp.model.transaction.TransactionType;
import com.bmsp.bmsp.repository.account.AccountRepository;
import com.bmsp.bmsp.repository.audit.AuditLogRepository;
import com.bmsp.bmsp.repository.loan.LoanRepository;
import com.bmsp.bmsp.repository.transaction.TransactionRepository;
import com.bmsp.bmsp.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public AdminDashboardStats getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long totalAccounts = accountRepository.count();
        long activeAccounts = accountRepository.countByStatus(AccountStatus.ACTIVE);
        
        // Fixed: Properly handle Optional return values
        BigDecimal totalBalance = accountRepository.sumTotalBalance();
        BigDecimal todayTransactionVolume = transactionRepository.sumTodayTransactions().orElse(BigDecimal.ZERO);
        
        long pendingLoans = loanRepository.countByStatus(LoanStatus.PENDING);

        // Fixed: Handle Optional return values from loan repository
        BigDecimal totalLoanAmount = loanRepository.sumTotalLoanAmount().orElse(BigDecimal.ZERO);
        BigDecimal totalOutstandingAmount = loanRepository.sumOutstandingAmount().orElse(BigDecimal.ZERO);

        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .totalBalance(totalBalance)
                .todayTransactionVolume(todayTransactionVolume)
                .pendingLoans(pendingLoans)
                .totalLoanAmount(totalLoanAmount)
                .totalOutstandingAmount(totalOutstandingAmount)
                .build();
    }

    @Override
    public Page<UserManagementResponse> getAllUsers(int page, int size, String search, String role) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;
        
        if (search != null && !search.isEmpty() && role != null && !role.isEmpty()) {
            usersPage = userRepository.findBySearchAndRole(search, role, pageable);
        } else if (search != null && !search.isEmpty()) {
            usersPage = userRepository.findBySearch(search, pageable);
        } else if (role != null && !role.isEmpty()) {
            usersPage = userRepository.findByRole(role, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.map(this::mapToUserManagementResponse);
    }

    @Override
    public UserDetailResponse getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<AccountSummary> accounts = accountRepository.findByUser(user).stream()
                .map(account -> AccountSummary.builder()
                        .accountNumber(account.getAccountNumber())
                        .accountType(account.getAccountType())
                        .balance(account.getBalance())
                        .status(account.getStatus())
                        .createdAt(account.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<LoanSummary> loans = loanRepository.findByUser(user).stream()
                .map(loan -> LoanSummary.builder()
                        .id(loan.getId())
                        .loanAccountNumber(loan.getLoanAccountNumber())
                        .loanType(loan.getLoanType())
                        .loanAmount(loan.getLoanAmount())
                        .status(loan.getStatus())
                        .createdAt(loan.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UserDetailResponse.builder()
                .user(mapToUserManagementResponse(user))
                .accounts(accounts)
                .loans(loans)
                .build();
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserStatus(Long userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setStatus(request.getStatus());
        if (request.getStatus() == UserStatus.SUSPENDED) {
            user.setSuspensionReason(request.getReason());
        } else {
            user.setSuspensionReason(null);
        }
        
        userRepository.save(user);
        
        // Log the action
        com.bmsp.bmsp.model.audit.AuditLog auditLog = new com.bmsp.bmsp.model.audit.AuditLog();
        auditLog.setAction("USER_STATUS_UPDATE");
        auditLog.setDetails("Updated user status to: " + request.getStatus() + ". Reason: " + request.getReason());
        auditLog.setTargetId(userId.toString());
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
        
        return getUserDetails(userId);
    }

    @Override
    public Page<TransactionAdminResponse> getAllTransactions(int page, int size, String type, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        TransactionType transactionType = null;
        if (type != null && !type.isEmpty()) {
            try {
                transactionType = TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If invalid type, return all transactions
            }
        }
        
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        Page<Transaction> transactions = transactionRepository.findByFilters(
            transactionType, startDateTime, endDateTime, pageable);
        
        return transactions.map(this::mapToTransactionAdminResponse);
    }

    @Override
    public LoanStatsResponse getLoanStatistics() {
        long totalLoans = loanRepository.count();
        long pendingLoans = loanRepository.countByStatus(LoanStatus.PENDING);
        long approvedLoans = loanRepository.countByStatus(LoanStatus.APPROVED);
        long disbursedLoans = loanRepository.countByStatus(LoanStatus.DISBURSED);
        long rejectedLoans = loanRepository.countByStatus(LoanStatus.REJECTED);
        long closedLoans = loanRepository.countByStatus(LoanStatus.CLOSED);
        long defaultedLoans = loanRepository.countByStatus(LoanStatus.DEFAULTED);
        
        // Fixed: Properly handle Optional return values
        BigDecimal totalLoanAmount = loanRepository.sumTotalLoanAmount().orElse(BigDecimal.ZERO);
        BigDecimal totalOutstandingAmount = loanRepository.sumOutstandingAmount().orElse(BigDecimal.ZERO);
        BigDecimal defaultedAmount = loanRepository.sumDefaultedAmount().orElse(BigDecimal.ZERO);
        BigDecimal recoveredAmount = loanRepository.sumRecoveredAmount();

        return LoanStatsResponse.builder()
                .totalLoans(totalLoans)
                .pendingLoans(pendingLoans)
                .approvedLoans(approvedLoans)
                .disbursedLoans(disbursedLoans)
                .rejectedLoans(rejectedLoans)
                .closedLoans(closedLoans)
                .defaultedLoans(defaultedLoans)
                .totalLoanAmount(totalLoanAmount)
                .totalOutstandingAmount(totalOutstandingAmount)
                .defaultedAmount(defaultedAmount)
                .recoveredAmount(recoveredAmount)
                .build();
    }

    @Override
    public TransactionAnalyticsResponse getTransactionAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        Long totalTransactions = transactionRepository.countByTransactionDateBetween(startDateTime, endDateTime);
        
        // Fixed: Properly handle Optional return values
        BigDecimal totalAmount = transactionRepository.sumAmountByTransactionDateBetween(startDateTime, endDateTime)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal averageTransaction = totalTransactions > 0 ? 
                totalAmount.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // Find peak hour by counting transactions per hour
        Integer peakHour = 12; // Default value
        List<Object[]> peakHourResults = transactionRepository.getPeakTransactionHour(startDateTime, endDateTime);
        if (!peakHourResults.isEmpty()) {
            peakHour = ((Number) peakHourResults.get(0)[0]).intValue();
        }

        return TransactionAnalyticsResponse.builder()
                .totalTransactions(totalTransactions)
                .totalAmount(totalAmount)
                .averageTransaction(averageTransaction)
                .peakHour(peakHour)
                .build();
    }

    @Override
    public CustomerGrowthResponse getCustomerGrowthAnalytics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        long totalCustomers = userRepository.count();
        
        long newCustomers = userRepository.countByCreatedAtBetween(startDateTime, endDateTime);
        long activeCustomers = userRepository.countByLastLoginAfter(LocalDateTime.now().minusDays(30));
        
        return CustomerGrowthResponse.builder()
                .totalCustomers(totalCustomers)
                .newCustomers(newCustomers)
                .activeCustomers(activeCustomers)
                .build();
    }

    @Override
    @Transactional
    public BulkOperationResponse bulkUpdateAccountStatus(List<String> accountNumbers, String status) {
        int totalProcessed = accountNumbers.size();
        int successCount = 0;
        int failureCount = 0;

        AccountStatus accountStatus;
        try {
            accountStatus = AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid account status: " + status);
        }

        for (String accountNumber : accountNumbers) {
            try {
                Optional<Account> accountOpt = accountRepository.findById(accountNumber);
                if (accountOpt.isPresent()) {
                    Account account = accountOpt.get();
                    account.setStatus(accountStatus);
                    accountRepository.save(account);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                failureCount++;
            }
        }

        return BulkOperationResponse.builder()
                .totalProcessed(totalProcessed)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    @Override
    public Page<AuditLogResponse> getAuditLogs(int page, int size, String action, LocalDate date) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startDateTime = date != null ? date.atStartOfDay() : null;
        LocalDateTime endDateTime = date != null ? date.atTime(23, 59, 59) : null;
        
        Page<com.bmsp.bmsp.model.audit.AuditLog> auditLogs;
        if (action != null && !action.isEmpty() && date != null) {
            auditLogs = auditLogRepository.findByActionAndTimestampBetween(action, startDateTime, endDateTime, pageable);
        } else if (action != null && !action.isEmpty()) {
            auditLogs = auditLogRepository.findByAction(action, pageable);
        } else if (date != null) {
            auditLogs = auditLogRepository.findByTimestampBetween(startDateTime, endDateTime, pageable);
        } else {
            auditLogs = auditLogRepository.findAll(pageable);
        }
        
        return auditLogs.map(auditLog -> AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .details(auditLog.getDetails())
                .targetId(auditLog.getTargetId())
                .timestamp(auditLog.getTimestamp())
                .build());
    }

    @Override
    public BroadcastResponse sendBroadcastNotification(String message, String userType, User admin) {
        int recipientsCount;
        if ("ALL".equalsIgnoreCase(userType)) {
            recipientsCount = (int) userRepository.count();
        } else if ("ACTIVE".equalsIgnoreCase(userType)) {
            recipientsCount = (int) userRepository.countByStatus(UserStatus.ACTIVE);
        } else {
            recipientsCount = 0;
        }
        
        return BroadcastResponse.builder()
                .message(message)
                .recipientsCount(recipientsCount)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private UserManagementResponse mapToUserManagementResponse(User user) {
        return UserManagementResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    private TransactionAdminResponse mapToTransactionAdminResponse(Transaction transaction) {
        return TransactionAdminResponse.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .referenceNumber(transaction.getReferenceNumber())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .balanceAfter(transaction.getBalanceAfter())
                .build();
    }
}