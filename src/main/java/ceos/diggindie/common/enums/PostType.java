package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostType {
    BOARD("BOARD", "자유게시판"),
    MARKET("MARKET", "거래게시판");

    private final String value;
    private final String displayName;
}