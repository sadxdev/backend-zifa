package com.bashverse.backendzifa.auth.service;

import com.bashverse.backendzifa.auth.domain.UserInfoResponse;
import com.bashverse.backendzifa.infra.redis.RedisUserService;
import com.bashverse.backendzifa.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RedisUserService redisUserService;  // Optional caching layer

    /**
     * Fetch user profile for given userId (e.g., Keycloak sub claim).
     *
     * Caches response for efficient repeated access.
     *
     * @param userId unique user ID from JWT
     * @return UserInfoResponse domain DTO
     */
    public UserInfoResponse getUserProfile(String userId) {
        // Try fetching from Redis cache first
        Optional<UserInfoResponse> cachedProfile = redisUserService.getUserProfileFromCache(userId);
        if (cachedProfile.isPresent()) {
            return cachedProfile.get();
        }

        // Fetch from database repository
        // Assuming UserRepository returns a User entity mapped to UserInfoResponse
        return userRepository.findByUserId(userId)
                .map(user -> {
                    UserInfoResponse profile = mapUserToUserInfoResponse(user);
                    // Cache the profile in Redis for 10-15 minutes TTL
                    redisUserService.cacheUserProfile(userId, profile);
                    return profile;
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserInfoResponse mapUserToUserInfoResponse(Object user) {
        // Map User entity fields to UserInfoResponse DTO fields
        // Replace Object with your actual User entity class
        String username = "";  // extract from user entity
        String email = "";     // extract from user entity
        List<String> roles = List.of(); // may extract from user or query Keycloak

        return new UserInfoResponse(username, email, roles);
    }
}
