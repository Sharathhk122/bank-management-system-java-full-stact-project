package com.bmsp.bmsp.repository.auth;

import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.auth.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    
    // Admin methods
    long countByStatus(UserStatus status);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    long countByLastLoginAfter(LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.riskScore > :riskScore")
    long countByRiskScoreGreaterThan(@Param("riskScore") int riskScore);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :timestamp")
    int countActiveSessions(@Param("timestamp") LocalDateTime timestamp);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :timestamp")
    int countByCreatedAtAfter(@Param("timestamp") LocalDateTime timestamp);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countNewUsers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.phone LIKE CONCAT('%', :search, '%'))")
    Page<User> findBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.phone LIKE CONCAT('%', :search, '%')) AND " +
           "r.name = :role")
    Page<User> findBySearchAndRole(@Param("search") String search, @Param("role") String role, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :role")
    Page<User> findByRole(@Param("role") String role, Pageable pageable);
    
    @Query("SELECT YEAR(u.createdAt) as year, MONTH(u.createdAt) as month, COUNT(u) as count " +
           "FROM User u " +
           "WHERE u.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY YEAR(u.createdAt), MONTH(u.createdAt) " +
           "ORDER BY YEAR(u.createdAt), MONTH(u.createdAt)")
    List<Object[]> getMonthlyUserGrowth(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT u.city, u.state, COUNT(u) " +
           "FROM User u " +
           "WHERE u.city IS NOT NULL AND u.state IS NOT NULL " +
           "GROUP BY u.city, u.state " +
           "ORDER BY COUNT(u) DESC")
    List<Object[]> getGeographicDistribution();
    
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
}