package com.bashverse.backendzifa.auth.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String RATE_LIMIT_KEY_PREFIX = "login_rate_limit:";
    private static final String FAILED_ATTEMPT_KEY_PREFIX = "login_failed_attempts:";
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final int BLOCK_TIME_SECONDS = 300; // 5 mins block on abuse

    public void checkRateLimit(String ip) {
        String key = RATE_LIMIT_KEY_PREFIX + ip;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }
        if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
            throw new RuntimeException("Too many login attempts. Please try again later.");
        }
    }

    public void recordFailedAttempt(String ip) {
        String key = FAILED_ATTEMPT_KEY_PREFIX + ip;
        Long fails = redisTemplate.opsForValue().increment(key);
        if (fails == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }
        if (fails != null && fails > MAX_REQUESTS_PER_MINUTE) {
            blockIP(ip);
        }
    }

    private void blockIP(String ip) {
        String key = RATE_LIMIT_KEY_PREFIX + ip;
        redisTemplate.opsForValue().set(key, MAX_REQUESTS_PER_MINUTE + 1, Duration.ofSeconds(BLOCK_TIME_SECONDS));
    }

    public void resetFailedAttempts(String ip) {
        String failKey = FAILED_ATTEMPT_KEY_PREFIX + ip;
        redisTemplate.delete(failKey);
    }

    public void cacheUserSession(Long userId, String token) {
        String sessionKey = "session:" + userId;
        redisTemplate.opsForValue().set(sessionKey, token, Duration.ofHours(1));
    }
}

