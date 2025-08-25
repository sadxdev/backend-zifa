package com.bashverse.backendzifa.auth.infra.redis;

import com.bashverse.backendzifa.auth.domain.UserInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUserService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;  // For JSON serialization

    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    public Optional<UserInfoResponse> getUserProfileFromCache(String userId) {
        String key = buildKey(userId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            UserInfoResponse profile = objectMapper.readValue(json, UserInfoResponse.class);
            return Optional.of(profile);
        } catch (JsonProcessingException e) {
            // Log and treat cache as miss
            return Optional.empty();
        }
    }

    public void cacheUserProfile(String userId, UserInfoResponse profile) {
        try {
            String json = objectMapper.writeValueAsString(profile);
            String key = buildKey(userId);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL);
        } catch (JsonProcessingException e) {
            // Log error and continue without caching
        }
    }

    private String buildKey(String userId) {
        return "user:profile:" + userId;
    }
}
