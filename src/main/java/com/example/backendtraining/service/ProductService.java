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
import com.example.backendtraining.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public ProductResponse getProducts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User user = currentUser();
        Page<Product> products;
        if (user.getRole() == Role.SELLER) {
            products = productRepo.findBySeller(currentSeller(), pageable);
        } else {
            products = productRepo.findAllWithSeller(pageable);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(products.getContent());
        productResponse.setPageNo(products.getNumber());
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalElements(products.getTotalElements());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setLast(products.isLast());
        return productResponse;
    }

    @Transactional(readOnly = true)
    public Product getProductById(int id) {
        Product product = productRepo.findByIdWithSeller(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        User user = currentUser();
        if (user.getRole() == Role.SELLER) {
            Seller seller = currentSeller();
            if (product.getSeller() == null || product.getSeller().getId() != seller.getId()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own products");
            }
        }
        return product;
    }

    public Product addProduct(ProductRequest request) {
        Seller seller = requireAcceptedSeller();
        if (request.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock cannot be negative");
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSeller(seller);
        return productRepo.save(product);
    }

    public List<Product> addProducts(List<ProductRequest> requests) {
        List<Product> saved = new ArrayList<>();
        for (ProductRequest request : requests) {
            saved.add(addProduct(request));
        }
        return saved;
    }

    public Product updateProduct(int id, ProductRequest request) {
        Product product = getProductById(id);
        requireOwnerOrAdmin(product);
        if (request.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock cannot be negative");
        }
        product.setName(request.getName());
        product.setProductType(request.getProductType());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
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
