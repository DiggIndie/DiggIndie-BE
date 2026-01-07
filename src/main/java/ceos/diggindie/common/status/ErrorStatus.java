package ceos.diggindie.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {

    // ==================== 공통 에러 ====================
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "리소스를 찾을 수 없습니다."),

    // ==================== 입력값 검증 ====================
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALID400", "입력값이 올바르지 않습니다."),

    // ==================== 인증/로그인 ====================
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH401", "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH402", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH403", "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH404", "리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."),

    // ==================== 회원 ====================
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER409", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER410", "이미 사용 중인 닉네임입니다."),

    // ==================== 아티스트 ====================
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTIST404", "아티스트를 찾을 수 없습니다."),

    // ==================== 앨범 ====================
    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "ALBUM404", "앨범을 찾을 수 없습니다."),

    // ==================== 좋아요 ====================
    ALREADY_LIKED(HttpStatus.CONFLICT, "LIKE409", "이미 좋아요를 눌렀습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE404", "좋아요 기록을 찾을 수 없습니다."),

    // ==================== 스크랩 ====================
    ALREADY_SCRAPPED(HttpStatus.CONFLICT, "SCRAP409", "이미 스크랩한 항목입니다."),
    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCRAP404", "스크랩 기록을 찾을 수 없습니다."),

    // ==================== 검색 ====================
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "SEARCH400", "검색어는 공백일 수 없습니다."),

    // ==================== 페이징 ====================
    INVALID_PAGE_REQUEST(HttpStatus.BAD_REQUEST, "PAGE400", "page는 0 이상, size는 1~100 사이여야 합니다."),

    // ==================== OAuth ====================
    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "OAUTH4001", "지원하지 않는 소셜 로그인 제공자입니다."),
    OAUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "OAUTH4002", "유효하지 않은 인가 코드입니다."),
    OAUTH_INVALID_PLATFORM(HttpStatus.BAD_REQUEST, "OAUTH4003", "유효하지 않은 플랫폼입니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "OAUTH5001", "소셜 로그인 토큰 요청에 실패했습니다."),
    OAUTH_USER_INFO_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "OAUTH5002", "소셜 로그인 사용자 정보 요청에 실패했습니다."),
    OAUTH_ALREADY_LINKED(HttpStatus.CONFLICT, "OAUTH4091", "이미 연동된 소셜 계정입니다."),
    OAUTH_ACCOUNT_EXISTS(HttpStatus.CONFLICT, "OAUTH4092", "해당 소셜 계정으로 가입된 회원이 이미 존재합니다."),
    OAUTH_UNLINK_DENIED(HttpStatus.BAD_REQUEST, "OAUTH4003", "마지막 로그인 수단은 연동 해제할 수 없습니다."),
    OAUTH_NOT_LINKED(HttpStatus.NOT_FOUND, "OAUTH4041", "연동되지 않은 소셜 계정입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}