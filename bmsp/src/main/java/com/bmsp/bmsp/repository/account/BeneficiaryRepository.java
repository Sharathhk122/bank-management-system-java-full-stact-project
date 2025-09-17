package com.bmsp.bmsp.repository.account;

import com.bmsp.bmsp.model.account.Beneficiary;
import com.bmsp.bmsp.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByUser(User user);
    Optional<Beneficiary> findByIdAndUser(Long id, User user);
    boolean existsByAccountNumberAndUser(String accountNumber, User user);
}