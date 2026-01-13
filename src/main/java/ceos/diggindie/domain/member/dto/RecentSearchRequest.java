package ceos.diggindie.domain.member.dto;

import ceos.diggindie.common.enums.SearchCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecentSearchRequest(
        @NotBlank(message = "검색어는 필수입니다.")
        String content,

        @NotNull(message = "검색 카테고리는 필수입니다.")
        SearchCategory category
) {}
