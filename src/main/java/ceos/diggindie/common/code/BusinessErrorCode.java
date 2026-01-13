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

    ;

    private final int statusCode;
    private final String message;
}