package com.example.backendtraining.config;

import com.example.backendtraining.Data_DBconnection.model.Admin;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.repository.AdminRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final AdminRepo adminRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AdminSeeder(UserRepo userRepo, AdminRepo adminRepo, BCryptPasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.countByRole(Role.ADMIN) > 0) {
            return;
        }
        if (userRepo.existsByEmail(adminEmail)) {
            return;
        }

        Admin admin = new Admin();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        adminRepo.save(admin);
        System.out.println("Default admin created: " + adminEmail);
    }
}
