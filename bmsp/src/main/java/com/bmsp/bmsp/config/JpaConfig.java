package com.bmsp.bmsp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.bmsp.bmsp.repository") // Changed from com.bank.repository
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        // Implementation to get current auditor (user)
        return () -> Optional.of("SYSTEM");
    }
}