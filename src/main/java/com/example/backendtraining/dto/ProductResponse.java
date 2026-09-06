package com.example.backendtraining.dto;

import com.example.backendtraining.Data_DBconnection.model.Product;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private List<Product> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

}
