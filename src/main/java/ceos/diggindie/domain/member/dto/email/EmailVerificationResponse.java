package ceos.diggindie.domain.member.dto.email;

import ceos.diggindie.common.enums.EmailType;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

import static ceos.diggindie.common.enums.EmailType.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailVerificationResponse(
        EmailType message,
        String resetToken,      // PASSWORD_RESET용
        String maskedUserId,    // FIND_USER_ID용
        LocalDateTime createdAt // FIND_USER_ID용
) {


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