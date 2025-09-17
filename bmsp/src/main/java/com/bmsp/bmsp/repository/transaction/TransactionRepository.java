package com.bmsp.bmsp.repository.transaction;

import com.bmsp.bmsp.model.account.Account;
import com.bmsp.bmsp.model.transaction.Transaction;
import com.bmsp.bmsp.model.transaction.TransactionType;
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
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOrderByTransactionDateDesc(Account account);
    Page<Transaction> findByAccountOrderByTransactionDateDesc(Account account, Pageable pageable);
    List<Transaction> findByAccountAndTransactionDateBetweenOrderByTransactionDateDesc(
            Account account, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByReferenceNumber(String referenceNumber);
    
    // Admin methods
    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE DATE(t.transaction_date) = CURRENT_DATE", nativeQuery = true)
    Optional<BigDecimal> sumTodayTransactions();
    
    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.transaction_date >= NOW() - INTERVAL 1 HOUR", nativeQuery = true)
    Optional<BigDecimal> sumTransactionsLastHour();
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :thirtyDaysAgo AND t.amount > 10000")
    int countSuspiciousTransactionsLast30Days();
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:startDate IS NULL OR t.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByFilters(@Param("type") TransactionType type,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.type IN ('INTEREST', 'FEE') AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateRevenue(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.type = 'INTEREST' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateInterestRevenue(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.type = 'FEE' AND " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> calculateFeeRevenue(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT YEAR(t.transactionDate) as year, MONTH(t.transactionDate) as month, t.type, COALESCE(SUM(t.amount), 0) " +
           "FROM Transaction t " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate), t.type " +
           "ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)")
    List<Object[]> getMonthlyRevenueBreakdown(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t WHERE " +
           "t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> findAverageTransactionValue(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT EXTRACT(HOUR FROM t.transaction_date) as hour, COUNT(t) as count " +
           "FROM transactions t " +
           "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(HOUR FROM t.transaction_date) " +
           "ORDER BY COUNT(t) DESC", nativeQuery = true)
    List<Object[]> getPeakTransactionHour(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t.type, COUNT(t) FROM Transaction t " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.type " +
           "ORDER BY COUNT(t) DESC")
    List<Object[]> getMostCommonTransactionType(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT EXTRACT(HOUR FROM t.transaction_date) as hour, COUNT(t), COALESCE(SUM(t.amount), 0) " +
           "FROM transactions t " +
           "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(HOUR FROM t.transaction_date) " +
           "ORDER BY EXTRACT(HOUR FROM t.transaction_date)", nativeQuery = true)
    List<Object[]> getHourlyTransactionPatterns(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query(value = "SELECT DAYOFWEEK(t.transaction_date) as day, COUNT(t), COALESCE(SUM(t.amount), 0) " +
           "FROM transactions t " +
           "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
           "GROUP BY DAYOFWEEK(t.transaction_date) " +
           "ORDER BY DAYOFWEEK(t.transaction_date)", nativeQuery = true)
    List<Object[]> getWeeklyTransactionPatterns(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    Long countByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumAmountByTransactionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
}