package ceos.diggindie.common.config.oauth;

import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.status.ErrorStatus;
import ceos.diggindie.domain.member.dto.oauth.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2Client {

    private final OAuth2Properties oAuth2Properties;
    private final RestClient restClient;

    public OAuth2UserInfo getUserInfo(LoginPlatform platform, String code) {
        String accessToken = getAccessToken(platform, code);
        Map<String, Object> userAttributes = getUserAttributes(platform, accessToken);
        return OAuth2UserInfo.of(platform, userAttributes);
    }

    private String getAccessToken(LoginPlatform platform, String code) {
        OAuth2Properties.Provider provider = oAuth2Properties.getProvider(platform.name());;

        String decodedCode;
        try {
            decodedCode = java.net.URLDecoder.decode(code, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            decodedCode = code;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", provider.getClientId());
        params.add("client_secret", provider.getClientSecret());
        params.add("redirect_uri", provider.getRedirectUri());
        params.add("code", decodedCode);

        if (platform == LoginPlatform.NAVER) {
            params.add("state", "STATE_STRING");
        }

        try {
            Map<String, Object> response = restClient.post()
                    .uri(provider.getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null || !response.containsKey("access_token")) {
                log.error("OAuth token response is empty or missing access_token: {}", response);
                throw new GeneralException(ErrorStatus.OAUTH_TOKEN_REQUEST_FAILED);
            }

            return (String) response.get("access_token");

        } catch (RestClientException e) {
            log.error("OAuth token request failed for {}: {}", platform, e.getMessage());
            throw new GeneralException(ErrorStatus.OAUTH_TOKEN_REQUEST_FAILED);
        }
    }

    private Map<String, Object> getUserAttributes(LoginPlatform platform, String accessToken) {
        OAuth2Properties.Provider provider = oAuth2Properties.getProvider(platform.name());

        try {
            RestClient.RequestHeadersSpec<?> request = restClient.get()
                    .uri(provider.getUserInfoUri())
                    .header("Authorization", "Bearer " + accessToken);

            if (platform == LoginPlatform.KAKAO) {
                request.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            }

            Map<String, Object> response = request
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null) {
                throw new GeneralException(ErrorStatus.OAUTH_USER_INFO_REQUEST_FAILED);
            }

            return response;

        } catch (RestClientException e) {
            log.error("OAuth user info request failed for {}: {}", platform, e.getMessage());
            throw new GeneralException(ErrorStatus.OAUTH_USER_INFO_REQUEST_FAILED);
        }
    }
}