package ceos.diggindie.domain.member.dto.auth;

public record SignupResponse(
        String externalId,
        String accessToken,
        long expiresIn,
        String userId,
        boolean isNew
) {
}
