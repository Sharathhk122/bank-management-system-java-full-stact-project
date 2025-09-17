package com.bmsp.bmsp.service.loan;

import com.bmsp.bmsp.dto.request.transaction.TransactionRequest;
import com.bmsp.bmsp.dto.response.loan.EMIScheduleResponse;
import com.bmsp.bmsp.exception.*;
import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.loan.*;
import com.bmsp.bmsp.repository.loan.EMIRepository;
import com.bmsp.bmsp.repository.loan.LoanRepository;
import com.bmsp.bmsp.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EMIServiceImpl implements EMIService {

    private final EMIRepository emiRepository;
    private final LoanRepository loanRepository;
    private final TransactionService transactionService;

    @Override
    @Transactional
    public void generateEMISchedule(Loan loan) {
        // First, check for and remove any existing duplicate EMI records
        cleanupDuplicateEMIRecords(loan);
        
        // Check if EMI records already exist to avoid duplicates
        List<EMIRecord> existingRecords = emiRepository.findByLoan(loan);
        if (!existingRecords.isEmpty()) {
            log.info("EMI schedule already exists for loan: {}", loan.getId());
            return;
        }

        List<EMIRecord> schedule = new ArrayList<>();
        BigDecimal remainingPrincipal = loan.getLoanAmount();
        BigDecimal monthlyInterestRate = loan.getInterestRate().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        LocalDate baseDate = loan.getStartDate() != null ? loan.getStartDate() : LocalDate.now();

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            LocalDate dueDate = baseDate.plusMonths(i);
            
            BigDecimal interestAmount = remainingPrincipal.multiply(monthlyInterestRate)
                    .setScale(2, RoundingMode.HALF_UP);
            
            BigDecimal principalAmount = loan.getEmiAmount().subtract(interestAmount);
            remainingPrincipal = remainingPrincipal.subtract(principalAmount);
            
            // Ensure remaining principal doesn't go negative due to rounding
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) < 0) {
                remainingPrincipal = BigDecimal.ZERO;
            }
            
            EMIRecord emiRecord = new EMIRecord();
            emiRecord.setLoan(loan);
            emiRecord.setInstallmentNumber(i);
            emiRecord.setDueDate(dueDate);
            emiRecord.setAmount(loan.getEmiAmount());
            emiRecord.setPrincipalAmount(principalAmount);
            emiRecord.setInterestAmount(interestAmount);
            emiRecord.setRemainingPrincipal(remainingPrincipal);
            emiRecord.setStatus(EMIRecord.EMIStatus.PENDING);
            
            schedule.add(emiRecord);
        }
        
        loan.setEmiRecords(schedule);
    }

    @Override
    @Transactional
    public void saveEMISchedule(Loan loan) {
        // First check for and remove any existing duplicate EMI records
        cleanupDuplicateEMIRecords(loan);
        
        // Check if EMI records already exist in database to avoid duplicates
        List<EMIRecord> existingRecords = emiRepository.findByLoan(loan);
        if (!existingRecords.isEmpty()) {
            log.info("EMI records already exist for loan: {}, skipping generation", loan.getId());
            return;
        }

        if (loan.getEmiRecords() == null || loan.getEmiRecords().isEmpty()) {
            generateEMISchedule(loan);
        }
        
        // Set the loan reference for each EMI record
        for (EMIRecord emiRecord : loan.getEmiRecords()) {
            emiRecord.setLoan(loan);
        }
        
        emiRepository.saveAll(loan.getEmiRecords());
        log.info("Saved {} EMI records for loan: {}", loan.getEmiRecords().size(), loan.getId());
    }

    @Override
    @Transactional
    public String payEMI(Long loanId, Integer installmentNumber, User user) {
        log.info("Processing EMI payment - Loan ID: {}, Installment: {}, User: {}", 
            loanId, installmentNumber, user.getId());
            
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        
        log.info("Loan status: {}, Linked account: {}", 
            loan.getStatus(), loan.getLinkedAccount() != null ? loan.getLinkedAccount().getAccountNumber() : "null");

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to pay this EMI");
        }

        if (loan.getStatus() != LoanStatus.DISBURSED) {
            throw new LoanException("Loan is not in disbursed state. Current status: " + loan.getStatus());
        }

        // Get all EMI records for this installment
        List<EMIRecord> emiRecords = emiRepository.findByLoanAndInstallmentNumber(loan, installmentNumber);
        
        if (emiRecords.isEmpty()) {
            throw new ResourceNotFoundException("EMI record not found for installment: " + installmentNumber);
        }

        // If there are duplicates, clean them up and get the first valid record
        EMIRecord emiRecord;
        if (emiRecords.size() > 1) {
            log.warn("Found {} duplicate EMI records for loan {} installment {}. Cleaning up...", 
                    emiRecords.size(), loanId, installmentNumber);
            cleanupDuplicateEMIRecordsForInstallment(loan, installmentNumber);
            
            // Get the first record again after cleanup
            emiRecord = emiRepository.findFirstByLoanAndInstallmentNumber(loan, installmentNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("EMI record not found after cleanup for installment: " + installmentNumber));
        } else {
            emiRecord = emiRecords.get(0);
        }

        if (emiRecord.getStatus() == EMIRecord.EMIStatus.PAID) {
            throw new LoanException("EMI installment #" + installmentNumber + " is already paid");
        }

        // Check if EMI is overdue
        if (emiRecord.getDueDate().isBefore(LocalDate.now())) {
            log.warn("EMI installment #{} is overdue. Due date: {}", installmentNumber, emiRecord.getDueDate());
            emiRecord.setStatus(EMIRecord.EMIStatus.LATE);
        }

        Account account = loan.getLinkedAccount();
        
        if (account == null) {
            throw new LoanException("No linked account found for this loan");
        }
        
        log.info("Account balance: {}, EMI amount: {}", account.getBalance(), emiRecord.getAmount());
        
        if (account.getBalance().compareTo(emiRecord.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to pay EMI. Required: " + 
                    emiRecord.getAmount() + ", Available: " + account.getBalance());
        }

        try {
            // Process EMI payment
            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setAccountNumber(account.getAccountNumber());
            transactionRequest.setAmount(emiRecord.getAmount());
            transactionRequest.setDescription("EMI payment for " + loan.getLoanAccountNumber() + 
                    " (Installment #" + installmentNumber + ")");

            transactionService.withdraw(transactionRequest, user);
            
            // Update EMI record
            emiRecord.setPaymentDate(LocalDate.now());
            emiRecord.setStatus(EMIRecord.EMIStatus.PAID);
            emiRepository.save(emiRecord);
            
            // Update loan status
            loan.setPaidAmount(loan.getPaidAmount().add(emiRecord.getAmount()));
            loan.setRecoveredAmount(loan.getRecoveredAmount().add(emiRecord.getPrincipalAmount()));
            
            // Check if all EMIs are paid
            long pendingEMIs = emiRepository.countByLoanAndStatus(loan, EMIRecord.EMIStatus.PENDING);
            if (pendingEMIs == 0) {
                loan.setStatus(LoanStatus.CLOSED);
                log.info("All EMIs paid. Loan {} is now CLOSED", loanId);
            }
            
            loanRepository.save(loan);
            
            return "EMI payment successful for installment #" + installmentNumber;
            
        } catch (Exception e) {
            log.error("Error processing EMI payment: {}", e.getMessage(), e);
            throw new LoanException("Failed to process EMI payment: " + e.getMessage());
        }
    }

    @Override
    public List<EMIScheduleResponse> getEMISchedule(Long loanId, User user) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this schedule");
        }

        List<EMIRecord> emiRecords = emiRepository.findByLoanOrderByInstallmentNumber(loan);
        
        return emiRecords.stream()
                .map(this::mapToEMIScheduleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cleanupDuplicateEMIRecords(Loan loan) {
        List<EMIRecord> allRecords = emiRepository.findByLoan(loan);
        
        // Group by installment number and remove duplicates
        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            final int installment = i;
            List<EMIRecord> duplicates = allRecords.stream()
                    .filter(r -> r.getInstallmentNumber() == installment)
                    .collect(Collectors.toList());
            
            if (duplicates.size() > 1) {
                log.warn("Found {} duplicate records for loan {} installment {}", 
                        duplicates.size(), loan.getId(), installment);
                
                // Keep the first record and delete the rest
                EMIRecord firstRecord = duplicates.get(0);
                for (int j = 1; j < duplicates.size(); j++) {
                    emiRepository.delete(duplicates.get(j));
                    log.info("Deleted duplicate EMI record ID: {}", duplicates.get(j).getId());
                }
            }
        }
    }

    @Override
    @Transactional
    public void cleanupDuplicateEMIRecordsForInstallment(Loan loan, Integer installmentNumber) {
        List<EMIRecord> duplicates = emiRepository.findByLoanAndInstallmentNumber(loan, installmentNumber);
        
        if (duplicates.size() > 1) {
            log.warn("Found {} duplicate records for loan {} installment {}", 
                    duplicates.size(), loan.getId(), installmentNumber);
            
            // Keep the first record and delete the rest
            EMIRecord firstRecord = duplicates.get(0);
            for (int i = 1; i < duplicates.size(); i++) {
                emiRepository.delete(duplicates.get(i));
                log.info("Deleted duplicate EMI record ID: {}", duplicates.get(i).getId());
            }
        }
    }

    private EMIScheduleResponse mapToEMIScheduleResponse(EMIRecord emiRecord) {
        return EMIScheduleResponse.builder()
                .installmentNumber(emiRecord.getInstallmentNumber())
                .dueDate(emiRecord.getDueDate())
                .amount(emiRecord.getAmount())
                .principalAmount(emiRecord.getPrincipalAmount())
                .interestAmount(emiRecord.getInterestAmount())
                .remainingPrincipal(emiRecord.getRemainingPrincipal())
                .status(emiRecord.getStatus())
                .paymentDate(emiRecord.getPaymentDate())
                .build();
    }
}