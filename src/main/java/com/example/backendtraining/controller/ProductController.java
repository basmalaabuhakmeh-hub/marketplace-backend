package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.dto.ProductRequest;
import com.example.backendtraining.dto.ProductResponse;
import com.example.backendtraining.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ProductResponse getProducts(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false)
            @Min(value = 0, message = "pageNo must be 0 or greater") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false)
            @Min(value = 1, message = "pageSize must be at least 1")
            @Max(value = 100, message = "pageSize must be at most 100") int pageSize) {
        return productService.getProducts(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @Min(value = 1, message = "Product id must be greater than 0") int id) {
        return productService.getProductById(id);
    }

    @PostMapping("")
    public Product addProduct(@Valid @RequestBody ProductRequest request) {
        return productService.addProduct(request);
    }

    @PostMapping("/bulk")
    public List<Product> addProducts(
            @RequestBody @NotEmpty(message = "Product list cannot be empty") List<@Valid ProductRequest> requests) {
        return productService.addProducts(requests);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable @Min(value = 1, message = "Product id must be greater than 0") int id,
            @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable @Min(value = 1, message = "Product id must be greater than 0") int id) {
        productService.deleteProduct(id);
    }
}
