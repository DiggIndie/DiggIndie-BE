package ceos.diggindie.domain.member.dto.auth;

public record LogoutResponse(
        String externalId,
        String userId
) {
}
