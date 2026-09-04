package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/sellers/pending")
    public List<Seller> getPendingSellers() {
        return adminService.getPendingSellers();
    }

    @PutMapping("/sellers/{id}/accept")
    public Seller acceptSeller(@PathVariable int id) {
        return adminService.acceptSeller(id);
    }

    @PutMapping("/sellers/{id}/reject")
    public Seller rejectSeller(@PathVariable int id) {
        return adminService.rejectSeller(id);
    }
}
