package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecommendSatisfactionReason {
    // 만족한 경우의 사유
    PERFECT_MATCH("취향에 딱 맞아요"),
    NEW_DISCOVERY("새로운 발견이 가능했어요"),

    // 불만족한 경우의 사유
    ALREADY_KNOWN("이미 알고 있는 아티스트에요"),
    NOT_MY_TASTE("취향과 상관없는 음악 같아요"),
    KEYWORD_MISMATCH("키워드와 실제 음악이 매칭되지 않아요"),
    GENRE_FINE_TRACK_NOT_MY_TASTE("장르는 맞는데 노래가 취향이 아니에요"),
    BORED("비슷한 스타일만 나와 지루해요"),
    OTHER("기타");

    private final String description;
}

