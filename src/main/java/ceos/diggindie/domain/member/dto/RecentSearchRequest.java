package ceos.diggindie.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record RecentSearchRequest(
        @NotBlank(message = "검색어는 필수입니다.")
        String content
) {}
