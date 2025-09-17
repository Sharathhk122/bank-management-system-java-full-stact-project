package com.bmsp.bmsp.repository.kyc;

import com.bmsp.bmsp.model.auth.User;
import com.bmsp.bmsp.model.kyc.KYC;
import com.bmsp.bmsp.model.kyc.KYCStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KYCRepository extends JpaRepository<KYC, Long> {
    Optional<KYC> findByUser(User user);
    boolean existsByUser(User user);
    
    // Check if user has approved KYC
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KYC k WHERE k.user = :user AND k.status = 'APPROVED'")
    boolean existsApprovedKYCByUser(@Param("user") User user);
    
    // Admin methods
    List<KYC> findByStatus(KYCStatus status);
    
    @Query("SELECT COUNT(k) FROM KYC k WHERE k.status = :status")
    long countByStatus(@Param("status") KYCStatus status);
    
    @Query(value = "SELECT * FROM kyc_details WHERE submitted_at >= CURRENT_DATE - INTERVAL 7 DAY", nativeQuery = true)
    List<KYC> findRecentSubmissions();
}