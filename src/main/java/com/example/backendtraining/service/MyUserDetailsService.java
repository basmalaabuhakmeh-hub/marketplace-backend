package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Customer;
import com.example.backendtraining.Data_DBconnection.model.UserPrincipel;
import com.example.backendtraining.Data_DBconnection.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyUserDetailsService implements UserDetailsService throws UsernameNotFoundException{

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = repo.findByUserName(username);

        if(user == null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");

        }
        returns new UserPrincipel()
    }
}
