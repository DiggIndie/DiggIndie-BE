package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailType {

    MSG_CODE_SENT("인증 코드가 발송되었습니다."),
    MSG_SIGNUP_SUCCESS("이메일 인증이 완료되었습니다."),
    MSG_PASSWORD_RESET_SUCCESS("인증이 완료되었습니다. 새 비밀번호를 설정해주세요."),
    MSG_FIND_ID_SUCCESS("아이디 찾기가 완료되었습니다.")
    ;

    private final String description;
}
