package ceos.diggindie.domain.member.dto;

public record LoginResponse(
        String externalId,
        String accessToken,
        long expiresIn,
        String userId,
        boolean isNew
) {
}

