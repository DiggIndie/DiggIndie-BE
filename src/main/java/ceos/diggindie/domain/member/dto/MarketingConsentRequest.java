package ceos.diggindie.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "마케팅 동의 변경 요청")
public record MarketingConsentRequest(
        @Schema(description = "마케팅 동의 여부", example = "true")
        @NotNull(message = "마케팅 동의 여부는 필수입니다.")
        Boolean marketingConsent
) {
}

