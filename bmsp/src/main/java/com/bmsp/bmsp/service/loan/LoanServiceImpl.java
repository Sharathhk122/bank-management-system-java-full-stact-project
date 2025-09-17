package com.bmsp.bmsp.service.loan;

import com.bmsp.bmsp.dto.request.loan.LoanRequest;
import com.bmsp.bmsp.dto.response.loan.LoanResponse;
import com.bmsp.bmsp.dto.response.loan.EMIScheduleResponse;
import com.bmsp.bmsp.exception.*;
import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.loan.*;
import com.bmsp.bmsp.repository.account.AccountRepository;
import com.bmsp.bmsp.repository.loan.LoanRepository;
import com.bmsp.bmsp.repository.loan.EMIRepository;
import com.bmsp.bmsp.service.kyc.KYCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EMIRepository emiRepository;
    private final AccountRepository accountRepository;
    private final EMIService emiService;
    private final KYCService kycService;

    @Override
    @Transactional
    public LoanResponse applyForLoan(LoanRequest request, User user) {
        try {
            validateLoanRequest(request);
            checkExistingLoans(user);
            Account account = verifyAccount(request, user);
            verifyKYCStatus(user);

            LoanCalculationResult calculation = calculateLoanDetails(request);
            Loan loan = createLoan(request, user, account, calculation);
            
            return mapToLoanResponse(loan);
        } catch (Exception e) {
            log.error("Error applying for loan for user {}: {}", user.getId(), e.getMessage());
            throw new LoanException("Failed to apply for loan: " + e.getMessage());
        }
    }

    @Override
    public LoanResponse getLoanDetails(Long loanId, User user) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this loan");
        }

        return mapToLoanResponse(loan);
    }

    @Override
    public List<LoanResponse> getUserLoans(User user) {
        return loanRepository.findByUser(user).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LoanResponse> getUserLoans(User user, Pageable pageable) {
        Page<Loan> loans = loanRepository.findByUserId(user.getId(), pageable);
        return loans.map(this::mapToLoanResponse);
    }

    @Override
    @Transactional
    public LoanResponse approveLoan(Long loanId, User adminUser) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException("Loan is not in pending state");
        }

        // Clean up any existing duplicate EMI records before generating new ones
        emiService.cleanupDuplicateEMIRecords(loan);
        
        // Set approval details and dates
        LocalDate startDate = LocalDate.now();
        loan.approve(startDate, adminUser.getEmail());

        // Check if EMI records already exist before generating new ones
        List<EMIRecord> existingRecords = emiRepository.findByLoan(loan);
        if (existingRecords.isEmpty()) {
            // Only generate EMI schedule if no records exist
            emiService.generateEMISchedule(loan);
            emiService.saveEMISchedule(loan);
        } else {
            log.info("EMI records already exist for loan {}, skipping generation", loanId);
            
            // Verify no duplicates in existing records
            for (EMIRecord record : existingRecords) {
                int count = emiRepository.countByLoanAndInstallmentNumber(loan, record.getInstallmentNumber());
                if (count > 1) {
                    log.warn("Found duplicate EMI records for loan {} installment {}", 
                            loanId, record.getInstallmentNumber());
                    emiService.cleanupDuplicateEMIRecordsForInstallment(loan, record.getInstallmentNumber());
                }
            }
        }

        Loan updatedLoan = loanRepository.save(loan);
        return mapToLoanResponse(updatedLoan);
    }

    @Override
    @Transactional
    public LoanResponse rejectLoan(Long loanId, String rejectionReason, User adminUser) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new LoanException("Loan is not in pending state");
        }

        loan.reject(rejectionReason, adminUser.getEmail());
        Loan updatedLoan = loanRepository.save(loan);
        return mapToLoanResponse(updatedLoan);
    }

    @Override
    public String payEMI(Long loanId, Integer installmentNumber, User user) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to pay this EMI");
        }

        return emiService.payEMI(loanId, installmentNumber, user);
    }

    @Override
    public List<LoanResponse> getPendingLoans() {
        return loanRepository.findByStatus(LoanStatus.PENDING, Pageable.unpaged()).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LoanResponse> getPendingLoans(Pageable pageable) {
        return loanRepository.findByStatus(LoanStatus.PENDING, pageable)
                .map(this::mapToLoanResponse);
    }

    @Override
    public List<EMIScheduleResponse> getEMISchedule(Long loanId, User user) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this schedule");
        }

        return emiService.getEMISchedule(loanId, user);
    }

    private void validateLoanRequest(LoanRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Loan request cannot be null");
        }
        if (request.getLoanAmount() == null || request.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be positive");
        }
        if (request.getTenureMonths() == null || request.getTenureMonths() <= 0) {
            throw new IllegalArgumentException("Tenure must be positive");
        }
        if (request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        if (request.getLoanType() == null) {
            throw new IllegalArgumentException("Loan type is required");
        }
    }

    private void checkExistingLoans(User user) {
        if (loanRepository.existsByUserAndStatusIn(user, 
                List.of(LoanStatus.PENDING, LoanStatus.APPROVED, LoanStatus.DISBURSED))) {
            throw new LoanException("You already have an active or pending loan");
        }
    }

    private Account verifyAccount(LoanRequest request, User user) {
        return accountRepository.findByAccountNumberAndUser(request.getAccountNumber(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found or not owned by user"));
    }

    private void verifyKYCStatus(User user) {
        if (!kycService.isKYCApproved(user)) {
            throw new KYCVerificationException("KYC verification is required and must be approved to apply for a loan");
        }
    }

    private LoanCalculationResult calculateLoanDetails(LoanRequest request) {
        BigDecimal interestRate = calculateInterestRate(request.getLoanType());
        BigDecimal emiAmount = calculateEMI(request.getLoanAmount(), interestRate, request.getTenureMonths());
        BigDecimal totalPayableAmount = emiAmount.multiply(BigDecimal.valueOf(request.getTenureMonths()));
        
        return new LoanCalculationResult(interestRate, emiAmount, totalPayableAmount);
    }

    private Loan createLoan(LoanRequest request, User user, Account account, LoanCalculationResult calculation) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLinkedAccount(account);
        loan.setLoanType(request.getLoanType());
        loan.setLoanAmount(request.getLoanAmount());
        loan.setInterestRate(calculation.interestRate);
        loan.setTenureMonths(request.getTenureMonths());
        loan.setEmiAmount(calculation.emiAmount);
        loan.setTotalPayableAmount(calculation.totalPayableAmount);
        loan.setStatus(LoanStatus.PENDING);
        loan.setStartDate(null); // Will be set when approved
        loan.setEndDate(null);   // Will be set when approved
        
        return loanRepository.save(loan);
    }

    private BigDecimal calculateInterestRate(LoanType loanType) {
        switch (loanType) {
            case HOME_LOAN: return new BigDecimal("8.5");
            case CAR_LOAN: return new BigDecimal("9.5");
            case PERSONAL_LOAN: return new BigDecimal("12.0");
            case EDUCATION_LOAN: return new BigDecimal("7.5");
            case BUSINESS_LOAN: return new BigDecimal("11.0");
            default: return new BigDecimal("10.0");
        }
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal rate, int tenure) {
        BigDecimal monthlyRate = rate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(tenure);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private LoanResponse mapToLoanResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .loanAccountNumber(loan.getLoanAccountNumber())
                .loanType(loan.getLoanType())
                .loanAmount(loan.getLoanAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .startDate(loan.getStartDate())
                .endDate(loan.getEndDate())
                .status(loan.getStatus())
                .emiAmount(loan.getEmiAmount())
                .totalPayableAmount(loan.getTotalPayableAmount())
                .paidAmount(loan.getPaidAmount())
                .recoveredAmount(loan.getRecoveredAmount())
                .rejectionReason(loan.getRejectionReason())
                .approvedBy(loan.getApprovedBy())
                .approvedAt(loan.getApprovedAt())
                .createdAt(loan.getCreatedAt())
                .build();
    }

    private static class LoanCalculationResult {
        final BigDecimal interestRate;
        final BigDecimal emiAmount;
        final BigDecimal totalPayableAmount;

        LoanCalculationResult(BigDecimal interestRate, BigDecimal emiAmount, BigDecimal totalPayableAmount) {
            this.interestRate = interestRate;
            this.emiAmount = emiAmount;
            this.totalPayableAmount = totalPayableAmount;
        }
    }

    public static class KYCVerificationException extends RuntimeException {
        public KYCVerificationException(String message) {
            super(message);
        }
    }
}