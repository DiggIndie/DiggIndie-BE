package ceos.diggindie.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailVerificationType {
    SIGNUP(
            "회원가입 인증",
            "아래 인증 코드를 입력하여 회원가입을 완료해주세요.",
            "#007bff",
            "[Diggindie] 회원가입 인증 코드"
    ),
    PASSWORD_RESET(
            "비밀번호 재설정",
            "아래 인증 코드를 입력하여 비밀번호를 재설정해주세요.",
            "#dc3545",
            "[Diggindie] 비밀번호 재설정 인증 코드"
    ),
    FIND_USER_ID(
            "아이디 찾기",
            "아래 인증 코드를 입력하여 아이디를 확인해주세요.",
            "#28a745",
            "[Diggindie] 아이디 찾기 인증 코드"
    );

    private final String title;
    private final String description;
    private final String color;
    private final String subject;
}