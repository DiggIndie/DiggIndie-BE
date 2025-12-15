package ceos.diggindie.domain.member.dto;

public record SignupRequest(
        String userId,
        String password,
        String email
) {
}
