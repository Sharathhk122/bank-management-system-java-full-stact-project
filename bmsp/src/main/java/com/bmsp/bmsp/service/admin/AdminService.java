package com.bmsp.bmsp.service.admin;

import com.bmsp.bmsp.dto.request.admin.UserStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.admin.*;
import com.bmsp.bmsp.model.auth.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {
    AdminDashboardStats getDashboardStats();
    Page<UserManagementResponse> getAllUsers(int page, int size, String search, String role);
    UserDetailResponse getUserDetails(Long userId);
    UserDetailResponse updateUserStatus(Long userId, UserStatusUpdateRequest request);
    Page<TransactionAdminResponse> getAllTransactions(int page, int size, String type, LocalDate startDate, LocalDate endDate);
    LoanStatsResponse getLoanStatistics();
    TransactionAnalyticsResponse getTransactionAnalytics(LocalDate startDate, LocalDate endDate);
    CustomerGrowthResponse getCustomerGrowthAnalytics(LocalDate startDate, LocalDate endDate);
    BulkOperationResponse bulkUpdateAccountStatus(List<String> accountNumbers, String status);
    Page<AuditLogResponse> getAuditLogs(int page, int size, String action, LocalDate date);
    BroadcastResponse sendBroadcastNotification(String message, String userType, User admin);
}