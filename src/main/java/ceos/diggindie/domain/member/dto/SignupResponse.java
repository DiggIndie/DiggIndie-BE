package ceos.diggindie.domain.member.dto;

public record SignupResponse(
        String externalId,
        String userId,
        boolean isNew
) {
}
