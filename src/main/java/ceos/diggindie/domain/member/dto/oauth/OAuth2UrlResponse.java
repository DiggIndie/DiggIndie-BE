package ceos.diggindie.domain.member.dto.oauth;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UrlResponse {

    @Schema(description = "OAuth2 인증 URL")
    private String authUrl;
}