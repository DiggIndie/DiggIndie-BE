package ceos.diggindie.domain.member.dto;

public record LogoutResponse(
        String memberId,
        String userId
) {
}
