package ceos.diggindie.domain.member.dto;

import ceos.diggindie.common.enums.SearchCategory;
import ceos.diggindie.domain.member.entity.RecentSearch;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class RecentSearchResponse {

    @Getter
    @Builder
    public static class RecentSearchInfo {
        private Long recentSearchId;
        private String content;
        private SearchCategory category;
        private LocalDateTime createdAt;

        public static RecentSearchInfo from(RecentSearch recentSearch) {
            return RecentSearchInfo.builder()
                    .recentSearchId(recentSearch.getId())
                    .content(recentSearch.getContent())
                    .category(recentSearch.getCategory())
                    .createdAt(recentSearch.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RecentSearchListDTO {
        private List<RecentSearchInfo> searches;

        public static RecentSearchListDTO from(List<RecentSearch> recentSearches) {
            List<RecentSearchInfo> searchInfos = recentSearches.stream()
                    .map(RecentSearchInfo::from)
                    .toList();
            return RecentSearchListDTO.builder()
                    .searches(searchInfos)
                    .build();
        }
    }
}
