package com.bmsp.bmsp.controller.admin;

import com.bmsp.bmsp.dto.request.admin.UserStatusUpdateRequest;
import com.bmsp.bmsp.dto.response.admin.*;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.security.UserDetailsImpl;
import com.bmsp.bmsp.service.admin.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        AdminDashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserManagementResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        Page<UserManagementResponse> users = adminService.getAllUsers(page, size, search, role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailResponse> getUserDetails(@PathVariable Long userId) {
        UserDetailResponse userDetails = adminService.getUserDetails(userId);
        return ResponseEntity.ok(userDetails);
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserDetailResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UserStatusUpdateRequest request) {
        UserDetailResponse updatedUser = adminService.updateUserStatus(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionAdminResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Page<TransactionAdminResponse> transactions = adminService.getAllTransactions(page, size, type, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/loans/stats")
    public ResponseEntity<LoanStatsResponse> getLoanStatistics() {
        LoanStatsResponse stats = adminService.getLoanStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/analytics/transactions")
    public ResponseEntity<TransactionAnalyticsResponse> getTransactionAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        TransactionAnalyticsResponse analytics = adminService.getTransactionAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/customers")
    public ResponseEntity<CustomerGrowthResponse> getCustomerGrowthAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        CustomerGrowthResponse analytics = adminService.getCustomerGrowthAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @PostMapping(value = "/bulk/accounts/status", consumes = "application/json")
    public ResponseEntity<BulkOperationResponse> bulkUpdateAccountStatus(
            @RequestBody List<String> accountNumbers,
            @RequestParam("status") String status) {
        BulkOperationResponse response = adminService.bulkUpdateAccountStatus(accountNumbers, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Page<AuditLogResponse> logs = adminService.getAuditLogs(page, size, action, date);
        return ResponseEntity.ok(logs);
    }

    @PostMapping("/notifications/broadcast")
    public ResponseEntity<BroadcastResponse> sendBroadcastNotification(
            @RequestParam String message,
            @RequestParam(required = false) String userType,
            Authentication authentication) {
        User admin = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
        BroadcastResponse response = adminService.sendBroadcastNotification(message, userType, admin);
        return ResponseEntity.ok(response);
    }
}