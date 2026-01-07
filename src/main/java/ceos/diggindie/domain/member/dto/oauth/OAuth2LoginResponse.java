package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2LoginResponse {

    @Schema(description = "신규 회원 여부")
    private boolean newMember;

    @Schema(description = "회원 ID (PK)")
    private Long memberId;

    @Schema(description = "사용자 ID")
    private String userId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "로그인 플랫폼")
    private LoginPlatform platform;

    @Schema(description = "Access Token")
    private String accessToken;
}