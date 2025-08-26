package com.bashverse.backendzifa.products.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String brand;

    // Add more fields as required for client needs,
    // but keep it minimal and avoid exposing sensitive info.
}
