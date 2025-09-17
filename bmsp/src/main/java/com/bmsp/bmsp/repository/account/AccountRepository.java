package com.bmsp.bmsp.repository.account;

import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.account.AccountStatus;
import com.bmsp.bmsp.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User user);
    Optional<Account> findByAccountNumberAndUser(String accountNumber, User user);
    boolean existsByAccountNumber(String accountNumber);
    
    // Admin methods
    long countByStatus(AccountStatus status);
    
    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.status = 'ACTIVE'")
    BigDecimal sumTotalBalance();
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.balance < a.minimumBalance AND a.status = 'ACTIVE'")
    int countHighRiskAccounts();
    
    @Query("SELECT a.accountType, COUNT(a), COALESCE(SUM(a.balance), 0) " +
           "FROM Account a " +
           "WHERE a.balance < a.minimumBalance AND a.status = 'ACTIVE' " +
           "GROUP BY a.accountType " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getTopRiskFactors();
}