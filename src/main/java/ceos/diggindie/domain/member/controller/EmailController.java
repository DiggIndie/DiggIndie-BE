package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.email.EmailSendRequest;
import ceos.diggindie.domain.member.dto.email.EmailVerifyRequest;
import ceos.diggindie.domain.member.dto.email.EmailVerificationResponse;
import ceos.diggindie.domain.member.service.AuthService;
import ceos.diggindie.domain.member.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
@Tag(name = "Email Verification", description = "이메일 인증 API")
public class EmailController {

    private final EmailService emailService;

    @Operation(
            summary = "인증 코드 발송",
            description = "회원가입/비밀번호 재설정/아이디 찾기를 위한 인증 코드를 이메일로 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "등록되지 않은 이메일 (PASSWORD_RESET, FIND_USER_ID)"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일 (SIGNUP)")
    })
    @PostMapping("/send")
    public ResponseEntity<Response<EmailVerificationResponse>> sendVerificationCode(
            @Valid @RequestBody EmailSendRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(SuccessCode.INSERT_SUCCESS, emailService.sendVerificationCode(request))
        );
    }

    @Operation(
            summary = "인증 코드 확인",
            description = "인증 코드를 확인합니다. PASSWORD_RESET은 resetToken 반환, FIND_USER_ID는 마스킹된 아이디 반환"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 불일치 또는 만료")
    })
    @PostMapping("/verify")
    public ResponseEntity<Response<EmailVerificationResponse>> verifyCode(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(SuccessCode.INSERT_SUCCESS, emailService.verifyCode(request))
        );
    }
}