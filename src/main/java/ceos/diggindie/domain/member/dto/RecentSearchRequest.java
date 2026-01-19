package ceos.diggindie.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecentSearchRequest(
        @NotBlank(message = "검색어는 필수입니다.")
        @Size(max = 100, message = "검색어는 100자 이내여야 합니다.")
        String content
) {}
