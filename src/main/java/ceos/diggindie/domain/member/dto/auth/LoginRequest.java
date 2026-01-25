package ceos.diggindie.domain.member.dto.auth;

public record LoginRequest(
        String userId,
        String password
) {
}

