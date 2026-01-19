package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchCategory {
    COMMUNITY("커뮤니티"),
    BAND("밴드"),
    CONCERT("공연"),
    GENERAL("일반");

    private final String description;
}
