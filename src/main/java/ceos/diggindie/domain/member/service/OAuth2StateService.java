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

    public String generateState(String platform, String purpose) {
        String state = UUID.randomUUID().toString();
        String key = STATE_PREFIX + state;

        // platform:purpose 형태로 저장 (예: "GOOGLE:login", "KAKAO:link")
        String value = platform + ":" + purpose;
        redisTemplate.opsForValue().set(key, value, STATE_TTL);

        return state;
    }

    public StateInfo validateAndConsume(String state) {
        if (state == null || state.isBlank()) {
            return null;
        }

        String key = STATE_PREFIX + state;
        String value = redisTemplate.opsForValue().getAndDelete(key);

        if (value == null || !value.contains(":")) {
            return null;
        }

        String[] parts = value.split(":", 2);
        return new StateInfo(parts[0], parts[1]);
    }

    public record StateInfo(
            String platform,
            String purpose
    ) {
        public boolean isLogin() {
            return "login".equalsIgnoreCase(purpose);
        }

        public boolean isLink() {
            return "link".equalsIgnoreCase(purpose);
        }
    }
}