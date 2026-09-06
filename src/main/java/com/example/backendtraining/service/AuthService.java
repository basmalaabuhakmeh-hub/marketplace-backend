package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.DriverRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.CustomerSignupRequest;
import com.example.backendtraining.dto.DriverSignupRequest;
import com.example.backendtraining.dto.LoginRequest;
import com.example.backendtraining.dto.SellerSignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    SellerRepo sellerRepo;
    @Autowired
    DriverRepo driverRepo;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTService jwtService;

    public void signupCustomer(CustomerSignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Customer customer = new Customer();
        customer.setName(req.getName());
        customer.setEmail(req.getEmail());
        customer.setPassword(passwordEncoder.encode(req.getPassword()));
        customer.setAddress(req.getAddress());
        customer.setPhoneNumber(req.getPhoneNumber());
        customer.setRole(Role.CUSTOMER);  // or rely on constructor
        customerRepo.save(customer);
    }

    public void signupSeller(SellerSignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        Seller seller = new Seller();
        seller.setName(req.getName());
        seller.setEmail(req.getEmail());
        seller.setPassword(passwordEncoder.encode(req.getPassword()));
        seller.setBusinessName(req.getBusinessName());
        seller.setRole(Role.SELLER);
        sellerRepo.save(seller);
    }

    public void signupDriver(DriverSignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        Driver driver = new Driver();
        driver.setName(req.getName());
        driver.setEmail(req.getEmail());
        driver.setPassword(passwordEncoder.encode(req.getPassword()));
        driver.setPhoneNumber(req.getPhoneNumber());
        driver.setRole(Role.DRIVER);
        driverRepo.save(driver);
    }

    public String login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        return jwtService.generateToken(req.getEmail());
    }
}
