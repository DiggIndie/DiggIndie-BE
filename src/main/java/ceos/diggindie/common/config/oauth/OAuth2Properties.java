package ceos.diggindie.common.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {

    private final Map<String, Provider> providers = new HashMap<>();

    @Getter
    @Setter
    public static class Provider {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String tokenUri;
        private String userInfoUri;
    }

    public Provider getProvider(String name) {
        return providers.get(name.toLowerCase());
    }
}