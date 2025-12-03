package ceos.diggindie.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements Code{

    // 인증 실패
    UNAUTHORIZED_ERROR(401,  "Unauthorized Exception"),

    // 권한 없음
    FORBIDDEN_ERROR(403,  "Forbidden Exception");

    private final int statusCode;
    private final String message;

}
