package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    public User register(@RequestBody User user){
        return userService.register(user);
    }
}
