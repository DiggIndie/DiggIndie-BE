package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertSortType {
    recent("recent", "공연 임박순"),
    view("view", "조회순"),
    scrap("scrap", "스크랩순");

    private final String value;
    private final String description;

}