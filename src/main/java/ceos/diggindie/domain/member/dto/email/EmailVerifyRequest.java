package ceos.diggindie.domain.member.dto.email;

import ceos.diggindie.common.enums.EmailVerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmailVerifyRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "인증 코드는 필수입니다.")
        String code,

        @NotNull(message = "인증 타입은 필수입니다.")
        EmailVerificationType type,

        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String newPassword  // PASSWORD_RESET일 때만 필수
) {}