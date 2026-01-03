package ceos.diggindie.domain.keyword.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record KeywordRequest(
        @NotNull(message = "키워드 ID 리스트는 필수입니다.")
        @Size(min = 1, message = "최소 1개 이상의 키워드를 선택해야 합니다.")
        List<Long> keywordIds
) {}