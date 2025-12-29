package ceos.diggindie.domain.member.dto;

public record LoginRequest(
        String userId,
        String password
) {
}

