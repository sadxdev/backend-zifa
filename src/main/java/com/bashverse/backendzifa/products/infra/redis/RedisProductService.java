package com.bashverse.backendzifa.products.infra.redis;

import com.bashverse.backendzifa.products.domain.ProductResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // Cache TTL to balance freshness and load (e.g., 30 seconds)
    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    /**
     * Fetches cached product responses from Redis
     *
     * @param cacheKey Cache key string
     * @return Optional List of ProductResponse, empty on cache miss or deserialization failure
     */
    public Optional<List<ProductResponse>> getProductsFromCache(String cacheKey) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if (json == null) {
            return Optional.empty();
        }
        try {
            List<ProductResponse> products = objectMapper.readValue(json, new TypeReference<List<ProductResponse>>() {});
            return Optional.of(products);
        } catch (Exception e) {
            // Log error as needed
            return Optional.empty();
        }
    }

    /**
     * Caches product response list as a JSON string in Redis with TTL
     *
     * @param cacheKey Cache key string
     * @param products List of ProductResponse to cache
     */
    public void cacheProducts(String cacheKey, List<ProductResponse> products) {
        try {
            String json = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(cacheKey, json, CACHE_TTL);
        } catch (Exception e) {
            // Log serialization error if needed
        }
    }
}
