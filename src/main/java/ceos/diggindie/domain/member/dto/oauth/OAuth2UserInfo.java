package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;

public record OAuth2UserInfo(
        LoginPlatform platform,
        String platformId,
        String email,
        String nickname,
        String profileImage
) {}