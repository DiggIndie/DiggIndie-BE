package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2LinkResponse {

    @Schema(description = "연동 성공 여부")
    private boolean success;

    @Schema(description = "연동된 플랫폼")
    private LoginPlatform platform;

    @Schema(description = "연동된 이메일")
    private String email;

    @Schema(description = "메시지")
    private String message;
}