package ceos.diggindie.domain.member.dto;

import ceos.diggindie.common.enums.RecommendSatisfactionReason;
import jakarta.validation.constraints.NotNull;

public record RecommendSatisfactionRequest(
        @NotNull(message = "만족도는 필수입니다.")
        Boolean isSatisfied,

        RecommendSatisfactionReason reason
) {}
