package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2UserInfo {

    private LoginPlatform platform;
    private String platformId;
    private String email;
    private String nickname;
    private String profileImage;

    public static OAuth2UserInfo of(LoginPlatform platform, Map<String, Object> attributes) {
        return switch (platform) {
            case KAKAO -> ofKakao(attributes);
            case NAVER -> ofNaver(attributes);
            case GOOGLE -> ofGoogle(attributes);
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        };
    }

    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;

        return OAuth2UserInfo.builder()
                .platform(LoginPlatform.KAKAO)
                .platformId(String.valueOf(attributes.get("id")))
                .email(kakaoAccount != null ? (String) kakaoAccount.get("email") : null)
                .nickname(profile != null ? (String) profile.get("nickname") : null)
                .profileImage(profile != null ? (String) profile.get("profile_image_url") : null)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .platform(LoginPlatform.NAVER)
                .platformId((String) response.get("id"))
                .email((String) response.get("email"))
                .nickname((String) response.get("name"))
                .profileImage((String) response.get("profile_image"))
                .build();
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .platform(LoginPlatform.GOOGLE)
                .platformId((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("name"))
                .profileImage((String) attributes.get("picture"))
                .build();
    }
}