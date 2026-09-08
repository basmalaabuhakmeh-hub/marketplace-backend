package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.Driver;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import com.example.backendtraining.Data_DBconnection.repository.DriverRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.CustomerSignupRequest;
import com.example.backendtraining.dto.DriverSignupRequest;
import com.example.backendtraining.dto.ForgotPasswordRequest;
import com.example.backendtraining.dto.LoginRequest;
import com.example.backendtraining.dto.ResetPasswordRequest;
import com.example.backendtraining.dto.SellerSignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

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
    @Autowired
    RestTemplate restTemplate;
    @Value("${otp.service.url}")
    private String otpServiceUrl;

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

    public void forgotPassword(ForgotPasswordRequest req) {
        User user = findActiveUser(req.getEmail());
        ForgotPasswordRequest otpRequest = new ForgotPasswordRequest();
        otpRequest.setEmail(user.getEmail());
        try {
            restTemplate.postForEntity(otpServiceUrl + "/otp/send", otpRequest, String.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "OTP service is not running");
        }
    }

    public void resetPassword(ResetPasswordRequest req) {
        if (req.getNewPassword() == null || req.getNewPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password is required");
        }
        User user = findActiveUser(req.getEmail());
        try {
            restTemplate.postForEntity(otpServiceUrl + "/otp/verify", req, String.class);
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "OTP service is not running");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepo.save(user);
    }

    private User findActiveUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }
}
