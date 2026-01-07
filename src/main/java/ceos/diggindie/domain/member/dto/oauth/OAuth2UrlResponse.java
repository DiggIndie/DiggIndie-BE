package ceos.diggindie.domain.member.dto.oauth;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UrlResponse {

    @Schema(description = "OAuth2 인증 URL")
    private String authUrl;

    @Schema(description = "CSRF 방지용 state (로그인 요청 시 함께 전송 필요)")
    private String state;
}