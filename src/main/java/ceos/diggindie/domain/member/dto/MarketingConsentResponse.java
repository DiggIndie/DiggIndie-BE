package ceos.diggindie.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "마케팅 동의 응답")
public record MarketingConsentResponse(
        @Schema(description = "마케팅 동의 여부", example = "true")
        boolean marketingConsent
) {
}

