package ceos.diggindie.common.enums;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardCategory {
    NONE("none", null),
    INFO("info", "정보"),
    REVIEW("review", "공연 후기"),
    RECOMMEND("recommend", "추천"),
    RELEASE("release", "신보"),
    NEWS("news", "음악 뉴스"),
    COMPANION("companion", "동행");

    private final String value;
    private final String description;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BoardCategory from(String value) {
        for (BoardCategory category : BoardCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new BusinessException(BusinessErrorCode.CATEGORY_BAD_REQUEST,
                "지원하지 않는 게시판 카테고리입니다: " + value +
                        ". (none, info, review, recommend, release, news, companion 중 선택해주세요.)");
    }
}