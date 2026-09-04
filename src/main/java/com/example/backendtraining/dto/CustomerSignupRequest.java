package com.example.backendtraining.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSignupRequest {
    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
}
