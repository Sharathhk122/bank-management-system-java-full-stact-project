package com.bmsp.bmsp.config;

import com.bmsp.bmsp.model.auth.ERole;
import com.bmsp.bmsp.model.auth.Role;
import com.bmsp.bmsp.repository.auth.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DatabaseInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            try {
                if (roleRepository.count() == 0) {
                    Role adminRole = new Role();
                    adminRole.setName(ERole.ROLE_ADMIN);
                    roleRepository.save(adminRole);

                    Role customerRole = new Role();
                    customerRole.setName(ERole.ROLE_CUSTOMER);
                    roleRepository.save(customerRole);
                }
            } catch (Exception e) {
                // Handle exception if tables don't exist yet
                // Hibernate will create them with ddl-auto=create-drop
                System.err.println("Error initializing database: " + e.getMessage());
            }
        };
    }
}