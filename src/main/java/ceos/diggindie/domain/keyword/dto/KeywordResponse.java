package ceos.diggindie.domain.keyword.dto;

import lombok.Builder;

@Builder
public record KeywordResponse(
        Long keywordId,
        String keyword
) {
    public static KeywordResponse from(ceos.diggindie.domain.keyword.entity.Keyword keyword) {
        return KeywordResponse.builder()
                .keywordId(keyword.getId())
                .keyword(keyword.getKeyword())
                .build();
    }
}