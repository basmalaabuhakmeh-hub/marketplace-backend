package com.example.backendtraining.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerSignupRequest {
    private String name;
    private String email;
    private String password;
    private String businessName;
}
