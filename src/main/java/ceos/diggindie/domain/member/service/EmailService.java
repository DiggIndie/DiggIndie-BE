package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.enums.EmailVerificationType;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.member.dto.PasswordResetRequest;
import ceos.diggindie.domain.member.dto.email.EmailSendRequest;
import ceos.diggindie.domain.member.dto.email.EmailVerifyRequest;
import ceos.diggindie.domain.member.dto.email.EmailVerificationResponse;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final RedissonClient redissonClient;
    private final JavaMailSender mailSender;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    private static final String CODE_PREFIX = "email_verify:";
    private static final String ATTEMPT_PREFIX = "email_attempt:";
    private static final long CODE_VALIDITY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;

    private static final String RESET_TOKEN_PREFIX = "password_reset:";
    private static final long RESET_TOKEN_VALIDITY_MINUTES = 10;


    public EmailVerificationResponse sendVerificationCode(EmailSendRequest request) {
        switch (request.type()) {
            case SIGNUP -> {
                if (memberRepository.existsByEmail(request.email())) {
                    throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS);
                }
            }
            case PASSWORD_RESET, FIND_USER_ID -> {
                if (!memberRepository.existsByEmail(request.email())) {
                    throw new BusinessException(BusinessErrorCode.EMAIL_NOT_REGISTERED);
                }
            }
        }

        String code = generateCode();
        saveCode(request.email(), code, request.type());
        sendVerificationEmail(request.email(), code, request.type());

        return new EmailVerificationResponse("인증 코드가 발송되었습니다.");
    }

    @Transactional
    public EmailVerificationResponse verifyCode(EmailVerifyRequest request) {
        // 브루트포스 체크
        checkAttemptLimit(request.email(), request.type());

        boolean isValid = validateCode(request.email(), request.code(), request.type());

        if (!isValid) {
            incrementAttempt(request.email(), request.type());
            throw new BusinessException(BusinessErrorCode.EMAIL_CODE_INVALID);
        }

        // 인증 성공 시 시도 횟수 초기화
        clearAttempt(request.email(), request.type());

        return switch (request.type()) {
            case SIGNUP -> new EmailVerificationResponse("이메일 인증이 완료되었습니다.");

            case PASSWORD_RESET -> {
                String resetToken = createResetToken(request.email());
                yield EmailVerificationResponse.forPasswordReset(
                        "인증이 완료되었습니다. 새 비밀번호를 설정해주세요.",
                        resetToken
                );
            }

            case FIND_USER_ID -> {
                Member member = findMemberByEmail(request.email());
                String maskedUserId = maskUserId(member.getUserId());
                yield EmailVerificationResponse.forFindUserId(
                        "아이디 찾기가 완료되었습니다.",
                        maskedUserId,
                        member.getCreatedAt()
                );
            }
        };
    }


    private void checkAttemptLimit(String email, EmailVerificationType type) {
        String attemptKey = buildAttemptKey(email, type);
        RAtomicLong attempts = redissonClient.getAtomicLong(attemptKey);

        if (attempts.get() >= MAX_ATTEMPTS) {
            throw new BusinessException(BusinessErrorCode.EMAIL_VERIFICATION_BLOCKED);
        }
    }

    private void incrementAttempt(String email, EmailVerificationType type) {
        String attemptKey = buildAttemptKey(email, type);
        RAtomicLong attempts = redissonClient.getAtomicLong(attemptKey);
        attempts.incrementAndGet();
        attempts.expire(Duration.ofMinutes(CODE_VALIDITY_MINUTES));
    }

    private void clearAttempt(String email, EmailVerificationType type) {
        String attemptKey = buildAttemptKey(email, type);
        redissonClient.getAtomicLong(attemptKey).delete();
    }

    private String buildAttemptKey(String email, EmailVerificationType type) {
        return ATTEMPT_PREFIX + type.name().toLowerCase() + ":" + email;
    }

    private void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException(BusinessErrorCode.PASSWORD_INVALID_FORMAT);
        }

        // 길이 체크: 6~20자
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            throw new BusinessException(BusinessErrorCode.PASSWORD_INVALID_FORMAT);
        }

        // 2가지 이상 조합 체크
        int typeCount = 0;
        if (newPassword.matches(".*[a-zA-Z].*")) typeCount++;  // 영문
        if (newPassword.matches(".*[0-9].*")) typeCount++;      // 숫자
        if (newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) typeCount++;  // 특수문자

        if (typeCount < 2) {
            throw new BusinessException(BusinessErrorCode.PASSWORD_INVALID_FORMAT);
        }
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.EMAIL_NOT_REGISTERED));
    }

    private String maskUserId(String userId) {
        if (userId.length() <= 3) {
            return userId.charAt(0) + "**";
        }
        return userId.substring(0, 3) + "*".repeat(userId.length() - 3);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void saveCode(String email, String code, EmailVerificationType type) {
        String key = buildKey(email, type);
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(code, CODE_VALIDITY_MINUTES, TimeUnit.MINUTES);
    }

    private boolean validateCode(String email, String code, EmailVerificationType type) {
        String key = buildKey(email, type);
        RBucket<String> bucket = redissonClient.getBucket(key);
        String storedCode = bucket.get();

        if (storedCode != null && storedCode.equals(code)) {
            bucket.delete();
            return true;
        }
        return false;
    }

    private String buildKey(String email, EmailVerificationType type) {
        return CODE_PREFIX + type.name().toLowerCase() + ":" + email;
    }

    private void sendVerificationEmail(String to, String code, EmailVerificationType type) {
        String subject = buildSubject(type);
        String content = buildContent(code, type);
        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException(BusinessErrorCode.EMAIL_SEND_FAILED);
        }
    }

    private String buildSubject(EmailVerificationType type) {
        return type.getSubject();
    }

    private String buildContent(String code, EmailVerificationType type) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif; padding: 20px;">
            <h2 style="color: #333;">Diggindie %s</h2>
            <p>%s</p>
            <div style="background-color: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;">
                <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: %s;">%s</span>
            </div>
            <p style="color: #666;">이 코드는 5분간 유효합니다.</p>
            <p style="color: #999; font-size: 12px;">본인이 요청하지 않은 경우 이 이메일을 무시해주세요.</p>
        </body>
        </html>
        """.formatted(type.getTitle(), type.getDescription(), type.getColor(), code);
    }

    private String createResetToken(String email) {
        String token = UUID.randomUUID().toString();
        String key = RESET_TOKEN_PREFIX + email;
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(token, RESET_TOKEN_VALIDITY_MINUTES, TimeUnit.MINUTES);
        return token;
    }

    private void validateResetToken(String email, String token) {
        String key = RESET_TOKEN_PREFIX + email;
        RBucket<String> bucket = redissonClient.getBucket(key);
        String stored = bucket.get();

        if (stored == null || !stored.equals(token)) {
            throw new BusinessException(BusinessErrorCode.INVALID_RESET_TOKEN);
        }
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        validateResetToken(request.email(), request.resetToken());
        validateNewPassword(request.newPassword());

        Member member = findMemberByEmail(request.email());
        member.updatePassword(passwordEncoder.encode(request.newPassword()));

        deleteResetToken(request.email());
        refreshTokenService.delete(member.getExternalId());
    }

    private void deleteResetToken(String email) {
        String key = RESET_TOKEN_PREFIX + email;
        redissonClient.getBucket(key).delete();
    }

}