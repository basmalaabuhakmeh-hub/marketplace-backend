package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.model.Role;
import com.example.backendtraining.Data_DBconnection.model.Seller;
import com.example.backendtraining.Data_DBconnection.model.SellerStatus;
import com.example.backendtraining.Data_DBconnection.model.User;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.Data_DBconnection.repository.SellerRepo;
import com.example.backendtraining.Data_DBconnection.repository.UserRepo;
import com.example.backendtraining.dto.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final SellerRepo sellerRepo;
    private final UserRepo userRepo;

    public ProductService(ProductRepo productRepo, SellerRepo sellerRepo, UserRepo userRepo) {
        this.productRepo = productRepo;
        this.sellerRepo = sellerRepo;
        this.userRepo = userRepo;
    }

    public List<Product> getProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(int id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product addProduct(ProductRequest request) {
        Seller seller = requireAcceptedSeller();
        Product product = new Product();
        product.setName(request.getName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setSeller(seller);
        return productRepo.save(product);
    }

    public Product updateProduct(int id, ProductRequest request) {
        Product product = getProductById(id);
        requireOwnerOrAdmin(product);
        product.setName(request.getName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        return productRepo.save(product);
    }

    public void deleteProduct(int id) {
        Product product = getProductById(id);
        requireOwnerOrAdmin(product);
        productRepo.deleteById(id);
    }

    private Seller requireAcceptedSeller() {
        Seller seller = currentSeller();
        if (seller.getStatus() != SellerStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller is not approved");
        }
        return seller;
    }

    private void requireOwnerOrAdmin(Product product) {
        User user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }
        Seller seller = currentSeller();
        if (seller.getStatus() != SellerStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller is not approved");
        }
        if (product.getSeller() == null || product.getSeller().getId() != seller.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change your own products");
        }
    }

    private Seller currentSeller() {
        return sellerRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Only sellers can manage products"));
    }

    private User currentUser() {
        return userRepo.findByEmail(currentEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
        }
        return authentication.getName();
    }
}
