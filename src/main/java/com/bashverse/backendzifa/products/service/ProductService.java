package com.bashverse.backendzifa.products.service;

import com.bashverse.backendzifa.products.domain.Product;
import com.bashverse.backendzifa.products.domain.ProductResponse;
import com.bashverse.backendzifa.products.infra.redis.RedisProductService;
import com.bashverse.backendzifa.products.infra.repository.ProductRepository;
import com.bashverse.backendzifa.products.util.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisProductService redisProductService;
    private final ProductMapper productMapper;

    /**
     * Get products optionally filtered by category with pagination.
     * Uses Redis cache to speed up repeated queries.
     *
     * @param category optional category filter
     * @param page     page number (0-based)
     * @param size     page size
     * @return list of ProductResponse DTOs
     */
    public List<ProductResponse> getProducts(Optional<String> category, int page, int size) {
        String cacheKey = buildCacheKey(category, page, size);

        Optional<List<ProductResponse>> cachedProducts = redisProductService.getProductsFromCache(cacheKey);
        if (cachedProducts.isPresent()) {
            return cachedProducts.get();
        }

        Pageable pageable = PageRequest.of(page, size);
        List<Product> products = category
                .map(cat -> productRepository.findActiveProductsByCategory(cat))
                .orElse(productRepository.findAll(pageable).getContent());

        List<ProductResponse> responses = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        redisProductService.cacheProducts(cacheKey, responses);
        return responses;
    }

    private String buildCacheKey(Optional<String> category, int page, int size) {
        return "products:" + category.orElse("all") + ":page:" + page + ":size:" + size;
    }
}
