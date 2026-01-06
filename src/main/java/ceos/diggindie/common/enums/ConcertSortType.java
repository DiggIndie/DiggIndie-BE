package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertSortType {
    RECENT("recent", "공연 임박순"),
    VIEW("view", "조회순"),
    SCRAP("scrap", "스크랩순");

    private final String value;
    private final String description;

}