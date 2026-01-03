package ceos.diggindie.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedissonClient redissonClient;
    private final String KEY_PREFIX = "refresh_token:";

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    public void save(String externalId, String token){
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + externalId);
        bucket.set(token, refreshTokenValidity.toMillis(), TimeUnit.MILLISECONDS);
    }

    public String get(String externalId){
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + externalId);
        return bucket.get();
    }

    public void delete(String externalId){
        RBucket<String> bucket = redissonClient.getBucket(KEY_PREFIX + externalId);
        bucket.delete();
    }

    public boolean validate(String externalId, String token){
        String storedToken = get(externalId);
        return storedToken != null && storedToken.equals(token);
    }

}
