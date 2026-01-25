package ceos.diggindie.domain.member.dto.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuth2CallbackRequest {
    private String code;
    private String state;
}