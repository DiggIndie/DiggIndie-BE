package ceos.diggindie.domain.member.dto;

public record EmailVerificationResponse(
        String message,
        boolean success
) {}