package ceos.diggindie.domain.member.dto.email;

public record EmailVerificationResponse(
        String message,
        boolean success,
        String userId  // FIND_USER_ID일 때만 반환
) {
    public EmailVerificationResponse(String message, boolean success) {
        this(message, success, null);
    }
}