package com.bashverse.backendzifa.products.api;

import com.bashverse.backendzifa.products.domain.ProductResponse;
import com.bashverse.backendzifa.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    /**
     * Retrieve paginated list of products, optionally filtered by category.
     * Request example: GET /products?category=electronics&page=0&size=20
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('USER')")  // Restrict access to authenticated users with USER role
    public List<ProductResponse> getProducts(
            @RequestParam Optional<String> category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Validate parameters (page >=0, size positive and reasonable)
        if (page < 0) throw new IllegalArgumentException("Page index must be non-negative");
        if (size <= 0 || size > 100) throw new IllegalArgumentException("Size must be between 1 and 100");

        return productService.getProducts(category, page, size);
    }
}
