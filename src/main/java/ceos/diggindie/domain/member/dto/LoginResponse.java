package ceos.diggindie.domain.member.dto;

public record LoginResponse(
        String externalId,
        String userId,
        boolean isNew
) {
}

