package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.domain.member.entity.SocialAccount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecentLoginPlatformResponse {
    private LoginPlatform platform;
    private LocalDateTime lastLoginAt;

    public static RecentLoginPlatformResponse from(SocialAccount socialAccount) {
        return RecentLoginPlatformResponse.builder()
                .platform(socialAccount.getPlatform())
                .lastLoginAt(socialAccount.getUpdatedAt())
                .build();
    }
}