package com.bashverse.backendzifa.products.infra.repository;

import com.bashverse.backendzifa.products.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // Fetch all active products, optionally filter by category (for GET /products)
    @Query("SELECT p FROM Product p WHERE p.active = true AND (:category IS NULL OR p.category = :category)")
    List<Product> findActiveProductsByCategory(@Param("category") String category);

    // Optional: Full-text search example
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchActiveProducts(@Param("searchTerm") String searchTerm);

    // Optional: Paging and sorting come built in with JpaRepository's Page<Product> findAll(Pageable pageable)
}
