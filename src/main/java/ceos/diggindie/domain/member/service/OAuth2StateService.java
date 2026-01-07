package ceos.diggindie.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2StateService {

    private static final String STATE_PREFIX = "oauth:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    /**
     * state 생성 및 Redis 저장
     */
    public String generateState(String platform) {
        String state = UUID.randomUUID().toString();
        String key = STATE_PREFIX + state;
        redisTemplate.opsForValue().set(key, platform, STATE_TTL);
        return state;
    }

    /**
     * state 검증 (일회성 - 검증 후 삭제)
     */
    public boolean validateAndConsume(String state, String expectedPlatform) {
        if (state == null || state.isBlank()) {
            return false;
        }

        String key = STATE_PREFIX + state;
        String platform = redisTemplate.opsForValue().getAndDelete(key);

        return expectedPlatform.equalsIgnoreCase(platform);
    }
}