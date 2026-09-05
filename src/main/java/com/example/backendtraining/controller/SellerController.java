package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.service.SellerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sellers")
@RestController
public class SellerController {
    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping("")
    public List<Seller> getSellers(){
        return sellerService.getSellers();
    }

    @GetMapping("/{id}")
    public Seller getSellerById(@PathVariable int id){
        return sellerService.getSellerById(id);
    }

    @PutMapping("")
    public void updateSeller(@RequestBody Seller cus){
        sellerService.updateSeller(cus);
    }

    @DeleteMapping("/{id}")
    public void deleteSeller(@PathVariable int id){
        sellerService.deleteSeller(id);
    }
}
