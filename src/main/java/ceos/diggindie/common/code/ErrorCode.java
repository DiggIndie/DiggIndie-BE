package ceos.diggindie.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements Code {

    // ==================== 공통 에러 ====================
    INTERNAL_SERVER_ERROR(500, "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),

    // ==================== 입력값 검증 ====================
    VALIDATION_ERROR(400, "입력값이 올바르지 않습니다."),

    // ==================== 인증/로그인 ====================
    LOGIN_REQUIRED(401, "로그인이 필요합니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."),

    // ==================== 회원 ====================
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(409, "이미 사용 중인 닉네임입니다."),

    // ==================== 아티스트 ====================
    ARTIST_NOT_FOUND(404, "아티스트를 찾을 수 없습니다."),

    // ==================== 앨범 ====================
    ALBUM_NOT_FOUND(404, "앨범을 찾을 수 없습니다."),

    // ==================== 좋아요 ====================
    ALREADY_LIKED(409, "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(404, "좋아요 기록을 찾을 수 없습니다."),

    // ==================== 스크랩 ====================
    ALREADY_SCRAPPED(409, "이미 스크랩한 항목입니다."),
    SCRAP_NOT_FOUND(404, "스크랩 기록을 찾을 수 없습니다."),

    // ==================== 검색 ====================
    INVALID_SEARCH_KEYWORD(400, "검색어는 공백일 수 없습니다."),

    // ==================== 페이징 ====================
    INVALID_PAGE_REQUEST(400, "page는 0 이상, size는 1~100 사이여야 합니다.");

    private final int statusCode;
    private final String message;
}
