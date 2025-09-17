package com.bmsp.bmsp.service.loan;

import com.bmsp.bmsp.dto.request.loan.LoanRequest;
import com.bmsp.bmsp.dto.response.loan.LoanResponse;
import com.bmsp.bmsp.dto.response.loan.EMIScheduleResponse;
import com.bmsp.bmsp.model.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {
    LoanResponse applyForLoan(LoanRequest request, User user);
    LoanResponse getLoanDetails(Long loanId, User user);
    List<LoanResponse> getUserLoans(User user);
    Page<LoanResponse> getUserLoans(User user, Pageable pageable);
    LoanResponse approveLoan(Long loanId, User adminUser);
    LoanResponse rejectLoan(Long loanId, String rejectionReason, User adminUser);
    String payEMI(Long loanId, Integer installmentNumber, User user);
    List<LoanResponse> getPendingLoans();
    Page<LoanResponse> getPendingLoans(Pageable pageable);
    List<EMIScheduleResponse> getEMISchedule(Long loanId, User user);
}