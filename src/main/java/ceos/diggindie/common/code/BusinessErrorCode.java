package ceos.diggindie.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode implements Code {

    // ==================== 회원 ====================
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    INVALID_CREDENTIALS(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    DUPLICATE_EMAIL(409, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USER_ID(409, "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(409, "이미 사용 중인 닉네임입니다."),
    RECENT_SEARCH_NOT_FOUND(404, "최근 검색어를 찾을 수 없습니다."),
    EMAIL_VERIFICATION_BLOCKED(429, "인증 시도 횟수를 초과했습니다. 잠시 후 다시 시도해주세요."),

    // ==================== 인증/토큰 ====================
    REFRESH_TOKEN_NOT_FOUND(401, "Refresh token이 존재하지 않습니다."),
    REFRESH_TOKEN_INVALID(401, "재로그인이 필요합니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),

    // ==================== 이메일 인증 ====================
    EMAIL_SEND_FAILED(500, "이메일 발송에 실패했습니다."),
    EMAIL_CODE_INVALID(400, "인증 코드가 올바르지 않거나 만료되었습니다."),
    EMAIL_NOT_REGISTERED(404, "등록되지 않은 이메일입니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 존재하는 이메일입니다."),
    PASSWORD_TOO_SHORT(400, "비밀번호는 최소 8자 이상이어야 합니다."),

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

    // ==================== 밴드 ====================
    BAND_NOT_FOUND(404, "밴드를 찾을 수 없습니다."),

    // ==================== Spotify ====================
    SPOTIFY_TOKEN_ERROR(500, "Spotify 토큰 발급에 실패했습니다."),
    SPOTIFY_ID_NOT_FOUND(404, "해당 밴드에 Spotify ID가 없습니다."),
    TOP_TRACK_NOT_FOUND(404, "해당 밴드의 Top Track을 찾을 수 없습니다."),

    // ==================== OAuth ====================
    OAUTH_PROVIDER_NOT_SUPPORTED(400, "지원하지 않는 소셜 로그인 제공자입니다."),
    OAUTH_CODE_INVALID(400, "유효하지 않은 인가 코드입니다."),
    OAUTH_INVALID_PLATFORM(400, "유효하지 않은 플랫폼입니다."),
    OAUTH_INVALID_STATE(400, "유효하지 않거나 만료된 state입니다."),
    OAUTH_UNLINK_DENIED(400, "마지막 로그인 수단은 연동 해제할 수 없습니다."),
    OAUTH_NOT_LINKED(404, "연동되지 않은 소셜 계정입니다."),
    OAUTH_ALREADY_LINKED(409, "이미 연동된 소셜 계정입니다."),
    OAUTH_ACCOUNT_EXISTS(409, "해당 소셜 계정으로 가입된 회원이 이미 존재합니다."),
    OAUTH_TOKEN_REQUEST_FAILED(502, "소셜 로그인 토큰 요청에 실패했습니다."),
    OAUTH_USER_INFO_REQUEST_FAILED(502, "소셜 로그인 사용자 정보 요청에 실패했습니다."),

    // ==================== 공연 ====================
    CONCERT_NOT_FOUND(404, "공연을 찾을 수 없습니다."),

    // ==================== 커뮤니티 ====================
    BOARD_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "해당 댓글을 찾을 수 없습니다."),
    CATEGORY_BAD_REQUEST(400, "지원하지 않는 게시판 카테고리입니다."),
    SELF_LIKE_NOT_ALLOWED(400, "자신의 게시글/댓글에는 좋아요할 수 없습니다."),
    BOARD_NOT_OWNER(403, "본인의 게시글만 수정/삭제할 수 있습니다."),

    MARKET_NOT_FOUND(404, "마켓 게시글을 찾을 수 없습니다."),
    MARKET_NOT_OWNER(403, "본인의 게시글만 수정/삭제할 수 있습니다."),
    SELF_SCRAP_NOT_ALLOWED(400, "자신의 게시글은 스크랩할 수 없습니다.")

    ;

    private final int statusCode;
    private final String message;
}
