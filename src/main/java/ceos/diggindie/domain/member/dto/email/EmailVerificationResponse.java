package ceos.diggindie.domain.member.dto.email;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailVerificationResponse(
        String message,
        String resetToken,      // PASSWORD_RESET용
        String maskedUserId,    // FIND_USER_ID용
        LocalDateTime createdAt // FIND_USER_ID용
) {
    // 기본 응답 (SIGNUP 등)
    public EmailVerificationResponse(String message) {
        this(message, null, null, null);
    }

    // PASSWORD_RESET용
    public static EmailVerificationResponse forPasswordReset(String message, String resetToken) {
        return new EmailVerificationResponse(message, resetToken, null, null);
    }

    // FIND_USER_ID용
    public static EmailVerificationResponse forFindUserId(String message, String maskedUserId, LocalDateTime createdAt) {
        return new EmailVerificationResponse(message, null, maskedUserId, createdAt);
    }
}