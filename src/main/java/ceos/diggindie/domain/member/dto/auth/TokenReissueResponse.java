package ceos.diggindie.domain.member.dto.auth;

public record TokenReissueResponse(
        String accessToken,
        Long expiresIn
) {

}
