package ceos.diggindie.domain.member.dto;

public record SignupResponse(
        String externalId,
        String accessToken,
        long expiresIn,
        String userId,
        boolean isNew
) {
}
