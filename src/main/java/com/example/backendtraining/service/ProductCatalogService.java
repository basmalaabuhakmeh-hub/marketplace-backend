package com.example.backendtraining.service;

import com.example.backendtraining.Data_DBconnection.model.Product;
import com.example.backendtraining.Data_DBconnection.repository.ProductRepo;
import com.example.backendtraining.dto.ProductResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCatalogService {

    private final ProductRepo productRepo;

    public ProductCatalogService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productCatalog", key = "#pageNo + '-' + #pageSize")
    public ProductResponse getCatalog(int pageNo, int pageSize) {
        Page<Product> products = productRepo.findAllWithSeller(PageRequest.of(pageNo, pageSize));
        ProductResponse response = new ProductResponse();
        response.setContent(products.getContent());
        response.setPageNo(products.getNumber());
        response.setPageSize(products.getSize());
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setLast(products.isLast());
        return response;
    }
}
