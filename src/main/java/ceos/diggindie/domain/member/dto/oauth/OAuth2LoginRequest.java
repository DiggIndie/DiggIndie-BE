package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuth2LoginRequest {

    @Schema(description = "인가 코드", example = "authorization_code_from_oauth_provider")
    @NotBlank(message = "인가 코드는 필수입니다.")
    private String code;

    @Schema(description = "소셜 로그인 플랫폼", example = "KAKAO")
    @NotNull(message = "플랫폼은 필수입니다.")
    private LoginPlatform platform;

    @Schema(description = "CSRF 방지용 state")
    @NotBlank(message = "state는 필수입니다.")
    private String state;
}