package com.bashverse.backendzifa.products.util;

import com.bashverse.backendzifa.products.domain.Product;
import com.bashverse.backendzifa.products.domain.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    /**
     * Converts Product entity to ProductResponse DTO for API output.
     *
     * @param product Product entity
     * @return ProductResponse DTO
     */
    public ProductResponse toDto(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getBrand()
        );
    }
}
