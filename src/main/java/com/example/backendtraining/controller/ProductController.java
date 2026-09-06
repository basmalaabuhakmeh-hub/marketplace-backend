package com.example.backendtraining.controller;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.dto.ProductRequest;
import com.example.backendtraining.dto.ProductResponse;
import com.example.backendtraining.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ProductResponse getProducts(@RequestParam(value = "pageNo", defaultValue = "0", required = false)int pageNo,
                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return productService.getProducts(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @PostMapping("")
    public Product addProduct(@RequestBody ProductRequest request) {
        return productService.addProduct(request);
    }

    @PostMapping("/bulk")
    public List<Product> addProducts(@RequestBody List<ProductRequest> requests) {
        return productService.addProducts(requests);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
    }
}
