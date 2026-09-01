package com.example.backendtraining;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {
    @RequestMapping("/")
    public String greet(){
        return "hello basmala";
    }

}
