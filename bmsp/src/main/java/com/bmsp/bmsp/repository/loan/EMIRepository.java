package com.bmsp.bmsp.repository.loan;

import com.bmsp.bmsp.model.loan.EMIRecord;
import com.bmsp.bmsp.model.loan.Loan;
import com.bmsp.bmsp.model.loan.EMIRecord.EMIStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EMIRepository extends JpaRepository<EMIRecord, Long> {
    List<EMIRecord> findByLoan(Loan loan);
    List<EMIRecord> findByLoanOrderByInstallmentNumber(Loan loan);
    List<EMIRecord> findByLoanAndInstallmentNumber(Loan loan, Integer installmentNumber);
    Optional<EMIRecord> findFirstByLoanAndInstallmentNumber(Loan loan, Integer installmentNumber);
    long countByLoanAndStatus(Loan loan, EMIStatus status);
    
    @Query("SELECT COUNT(e) FROM EMIRecord e WHERE e.loan = :loan AND e.installmentNumber = :installmentNumber")
    int countByLoanAndInstallmentNumber(@Param("loan") Loan loan, @Param("installmentNumber") Integer installmentNumber);
    
    @Query("SELECT e FROM EMIRecord e WHERE e.loan = :loan AND e.installmentNumber = :installmentNumber")
    List<EMIRecord> findByLoanAndInstallmentNumberQuery(@Param("loan") Loan loan, @Param("installmentNumber") Integer installmentNumber);
}