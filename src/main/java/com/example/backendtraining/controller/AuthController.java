package com.example.backendtraining.controller;

import com.example.backendtraining.dto.CustomerSignupRequest;
import com.example.backendtraining.dto.DriverSignupRequest;
import com.example.backendtraining.dto.ForgotPasswordRequest;
import com.example.backendtraining.dto.LoginRequest;
import com.example.backendtraining.dto.ResetPasswordRequest;
import com.example.backendtraining.dto.SellerSignupRequest;
import com.example.backendtraining.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup/customer")
    public ResponseEntity<String> signupCustomer(@RequestBody CustomerSignupRequest req) {
        authService.signupCustomer(req);
        return ResponseEntity.ok("Customer registered");
    }

    @PostMapping("/signup/seller")
    public ResponseEntity<String> signupSeller(@RequestBody SellerSignupRequest req) {
        authService.signupSeller(req);
        return ResponseEntity.ok("Seller registered — waiting for admin approval");
    }

    @PostMapping("/signup/driver")
    public ResponseEntity<String> signupDriver(@RequestBody DriverSignupRequest req) {
        authService.signupDriver(req);
        return ResponseEntity.ok("Driver registered — waiting for admin approval");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok("Password updated");
    }
}