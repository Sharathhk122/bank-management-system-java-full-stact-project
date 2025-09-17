// Beneficiary.java
package com.bmsp.bmsp.model.account;

import com.bmsp.bmsp.model.auth.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "beneficiaries")
@Data
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String bankName;

    private String branchName;
    private String ifscCode;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}