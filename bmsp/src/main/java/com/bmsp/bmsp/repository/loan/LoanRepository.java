package com.bmsp.bmsp.repository.loan;

import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.loan.Loan;
import com.bmsp.bmsp.model.loan.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);
    Page<Loan> findByUserId(Long userId, Pageable pageable);
    Optional<Loan> findByLoanAccountNumber(String loanAccountNumber);
    boolean existsByUserAndStatusIn(User user, List<LoanStatus> statuses);
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
    
    // Add the missing methods
    long countByStatus(LoanStatus status);
    
    @Query("SELECT COALESCE(SUM(l.loanAmount), 0) FROM Loan l")
    Optional<BigDecimal> sumTotalLoanAmount();
    
    @Query("SELECT COALESCE(SUM(l.loanAmount - l.paidAmount), 0) FROM Loan l WHERE l.status IN ('DISBURSED', 'DEFAULTED')")
    Optional<BigDecimal> sumOutstandingAmount();
    
    @Query("SELECT COALESCE(SUM(l.loanAmount - l.paidAmount), 0) FROM Loan l WHERE l.status = 'DEFAULTED'")
    Optional<BigDecimal> sumDefaultedAmount();
    
    @Query("SELECT COALESCE(SUM(l.paidAmount), 0) FROM Loan l WHERE l.status = 'CLOSED'")
    BigDecimal sumRecoveredAmount();
    
    @Query("SELECT l FROM Loan l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<Loan> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM Loan l WHERE l.user = :user AND (l.status = 'PENDING' OR l.status = 'APPROVED' OR l.status = 'DISBURSED')")
    List<Loan> findActiveOrPendingLoansByUser(@Param("user") User user);
}