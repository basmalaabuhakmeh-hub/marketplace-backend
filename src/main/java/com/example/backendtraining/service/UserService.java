package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo repo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public User register(User user){
        user.setPassword(encoder.encode((user.getPassword())));
        return repo.save(user);
    }
}
