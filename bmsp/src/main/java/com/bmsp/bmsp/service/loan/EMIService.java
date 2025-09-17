package com.bmsp.bmsp.service.loan;

import com.bmsp.bmsp.dto.response.loan.EMIScheduleResponse;
import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.loan.Loan;

import java.util.List;

public interface EMIService {
    void generateEMISchedule(Loan loan);
    void saveEMISchedule(Loan loan);
    String payEMI(Long loanId, Integer installmentNumber, User user);
    List<EMIScheduleResponse> getEMISchedule(Long loanId, User user);
    
    // Add these methods for duplicate handling
    void cleanupDuplicateEMIRecords(Loan loan);
    void cleanupDuplicateEMIRecordsForInstallment(Loan loan, Integer installmentNumber);
}