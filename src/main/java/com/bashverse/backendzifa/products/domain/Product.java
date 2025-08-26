package com.bashverse.backendzifa.products.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 255)
    private String brand;

    @Column(nullable = false)
    private Boolean active;

    // Extend with more fields as needed:
    // images, attributes, variants, createdAt, updatedAt, etc.
}
