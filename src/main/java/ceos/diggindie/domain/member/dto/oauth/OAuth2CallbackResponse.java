package ceos.diggindie.domain.member.dto.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2CallbackResponse {

    private String type;  // "login" 또는 "link"
    private LoginData loginData;
    private LinkData linkData;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginData {
        private boolean newMember;
        private String externalId;
        private String userId;
        private String email;
        private LoginPlatform platform;
        private String accessToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkData {
        private boolean success;
        private LoginPlatform platform;
        private String email;
        private String message;
    }

    public static OAuth2CallbackResponse login(OAuth2LoginResponse result) {
        return OAuth2CallbackResponse.builder()
                .type("login")
                .loginData(LoginData.builder()
                        .newMember(result.isNewMember())
                        .externalId(result.getExternalId())
                        .userId(result.getUserId())
                        .email(result.getEmail())
                        .platform(result.getPlatform())
                        .accessToken(result.getAccessToken())
                        .build())
                .build();
    }

    public static OAuth2CallbackResponse link(OAuth2LinkResponse result) {
        return OAuth2CallbackResponse.builder()
                .type("link")
                .linkData(LinkData.builder()
                        .success(result.isSuccess())
                        .platform(result.getPlatform())
                        .email(result.getEmail())
                        .message(result.getMessage())
                        .build())
                .build();
    }
}