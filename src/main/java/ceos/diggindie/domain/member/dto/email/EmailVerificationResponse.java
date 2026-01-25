package ceos.diggindie.domain.member.dto.email;

import java.time.LocalDateTime;

public record EmailVerificationResponse(
        String message,
        boolean success,
        String userId,  // FIND_USER_ID일 때만 반환
        LocalDateTime createdAt
) {
    public EmailVerificationResponse(String message, boolean success) {
        this(message, success, null, null);
    }

    public EmailVerificationResponse(String message, boolean success, String userId) {
        this(message, success, userId, null);
    }
}