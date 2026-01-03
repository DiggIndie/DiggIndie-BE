package ceos.diggindie.domain.member.dto;

public record LogoutResponse(
        String externalId,
        String userId
) {
}
