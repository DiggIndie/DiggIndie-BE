package ceos.diggindie.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedissonClient redissonClient;
    
    private static final String SIGNUP_CODE_PREFIX = "email_verify:signup:";
    private static final String PASSWORD_RESET_CODE_PREFIX = "email_verify:password:";
    private static final long CODE_VALIDITY_MINUTES = 5;

    /**
     * 6자리 인증 코드 생성
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 회원가입용 인증 코드 저장
     */
    public void saveSignupCode(String email, String code) {
        RBucket<String> bucket = redissonClient.getBucket(SIGNUP_CODE_PREFIX + email);
        bucket.set(code, CODE_VALIDITY_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 회원가입용 인증 코드 검증
     */
    public boolean verifySignupCode(String email, String code) {
        RBucket<String> bucket = redissonClient.getBucket(SIGNUP_CODE_PREFIX + email);
        String storedCode = bucket.get();
        
        if (storedCode != null && storedCode.equals(code)) {
            bucket.delete();
            return true;
        }
        return false;
    }

    /**
     * 비밀번호 재설정용 인증 코드 저장
     */
    public void savePasswordResetCode(String email, String code) {
        RBucket<String> bucket = redissonClient.getBucket(PASSWORD_RESET_CODE_PREFIX + email);
        bucket.set(code, CODE_VALIDITY_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 비밀번호 재설정용 인증 코드 검증
     */
    public boolean verifyPasswordResetCode(String email, String code) {
        RBucket<String> bucket = redissonClient.getBucket(PASSWORD_RESET_CODE_PREFIX + email);
        String storedCode = bucket.get();
        
        if (storedCode != null && storedCode.equals(code)) {
            bucket.delete();
            return true;
        }
        return false;
    }

    /**
     * 비밀번호 재설정 인증 완료 표시 저장 (비밀번호 변경 시까지 유효)
     */
    public void markPasswordResetVerified(String email) {
        RBucket<String> bucket = redissonClient.getBucket(PASSWORD_RESET_CODE_PREFIX + "verified:" + email);
        bucket.set("verified", 10, TimeUnit.MINUTES);
    }

    /**
     * 비밀번호 재설정 인증 완료 여부 확인
     */
    public boolean isPasswordResetVerified(String email) {
        RBucket<String> bucket = redissonClient.getBucket(PASSWORD_RESET_CODE_PREFIX + "verified:" + email);
        String value = bucket.get();
        return "verified".equals(value);
    }

    /**
     * 비밀번호 재설정 인증 완료 표시 삭제
     */
    public void clearPasswordResetVerified(String email) {
        RBucket<String> bucket = redissonClient.getBucket(PASSWORD_RESET_CODE_PREFIX + "verified:" + email);
        bucket.delete();
    }
}
