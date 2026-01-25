package ceos.diggindie.domain.member.dto;

import ceos.diggindie.common.enums.RecommendSatisfactionReason;
import ceos.diggindie.domain.member.entity.RecommendSatisfaction;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RecommendSatisfactionResponse {

    @Getter
    @Builder
    public static class RecommendSatisfactionInfo {
        private Long recommendSatisfactionId;
        private Boolean isSatisfied;
        private RecommendSatisfactionReason reason;
        private LocalDateTime createdAt;

        public static RecommendSatisfactionInfo from(RecommendSatisfaction recommendSatisfaction) {
            return RecommendSatisfactionInfo.builder()
                    .recommendSatisfactionId(recommendSatisfaction.getId())
                    .isSatisfied(recommendSatisfaction.getIsSatisfied())
                    .reason(recommendSatisfaction.getReason())
                    .createdAt(recommendSatisfaction.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RecommendSatisfactionListDTO {
        private List<RecommendSatisfactionInfo> satisfactions;

        public static RecommendSatisfactionListDTO from(List<RecommendSatisfaction> recommendSatisfactions) {
            List<RecommendSatisfactionInfo> satisfactionInfos = recommendSatisfactions.stream()
                    .map(RecommendSatisfactionInfo::from)
                    .toList();
            return RecommendSatisfactionListDTO.builder()
                    .satisfactions(satisfactionInfos)
                    .build();
        }
    }
}

