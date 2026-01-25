package ceos.diggindie.domain.member.dto.auth;

public record LoginResponse(
        String externalId,
        String accessToken,
        long expiresIn,
        String userId,
        boolean isNew
) {
}

