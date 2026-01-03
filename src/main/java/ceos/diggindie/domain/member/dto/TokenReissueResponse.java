package ceos.diggindie.domain.member.dto;

public record TokenReissueResponse(
        String accessToken,
        Long expiresIn
) {

}
