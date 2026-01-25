package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LinkedSocialAccountResponse {

    @Schema(description = "연동된 소셜 계정 목록")
    private List<SocialAccountInfo> accounts;

    @Getter
    @Builder
    public static class SocialAccountInfo {
        private LoginPlatform platform;
        private String email;
        private LocalDateTime connectedAt;
    }
}