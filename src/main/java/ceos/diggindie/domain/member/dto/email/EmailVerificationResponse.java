package ceos.diggindie.domain.member.dto.email;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailVerificationResponse(
        String message,
        String resetToken,      // PASSWORD_RESET용
        String maskedUserId,    // FIND_USER_ID용
        LocalDateTime createdAt // FIND_USER_ID용
) {
    private static final String MSG_CODE_SENT = "인증 코드가 발송되었습니다.";
    private static final String MSG_SIGNUP_SUCCESS = "이메일 인증이 완료되었습니다.";
    private static final String MSG_PASSWORD_RESET_SUCCESS = "인증이 완료되었습니다. 새 비밀번호를 설정해주세요.";
    private static final String MSG_FIND_ID_SUCCESS = "아이디 찾기가 완료되었습니다.";

    public static EmailVerificationResponse codeSent() {
        return new EmailVerificationResponse(MSG_CODE_SENT, null, null, null);
    }

    // 회원가입 등 기본 인증 성공 (SIGNUP)
    public static EmailVerificationResponse successSignup() {
        return new EmailVerificationResponse(MSG_SIGNUP_SUCCESS, null, null, null);
    }

    // 비밀번호 재설정용 (PASSWORD_RESET)
    public static EmailVerificationResponse successPasswordReset(String resetToken) {
        return new EmailVerificationResponse(MSG_PASSWORD_RESET_SUCCESS, resetToken, null, null);
    }

    // 아이디 찾기용 (FIND_USER_ID)
    public static EmailVerificationResponse successFindUserId(String maskedUserId, LocalDateTime createdAt) {
        return new EmailVerificationResponse(MSG_FIND_ID_SUCCESS, null, maskedUserId, createdAt);
    }
}