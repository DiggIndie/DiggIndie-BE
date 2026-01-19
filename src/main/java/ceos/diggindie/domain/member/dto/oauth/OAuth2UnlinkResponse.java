package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UnlinkResponse {

    @Schema(description = "연동 해제 성공 여부")
    private boolean success;

    @Schema(description = "연동 해제된 플랫폼")
    private LoginPlatform platform;

    @Schema(description = "메시지")
    private String message;
}