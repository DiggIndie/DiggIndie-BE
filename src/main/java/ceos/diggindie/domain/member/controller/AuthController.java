package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.*;
import ceos.diggindie.domain.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    @PostMapping("/auth/signup")
    public ResponseEntity<Response<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest,
            HttpServletResponse httpResponse
    ) {

        Response<SignupResponse> response = Response.of(
                SuccessCode.INSERT_SUCCESS,
                true,
                "회원 가입 API",
                authService.signup(signupRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<Response<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {

        Response<LoginResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "일반 로그인 API",
                authService.login(loginRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/auth/exists")
    public ResponseEntity<Response<UserIdCheckResponse>> checkExists(
            @Parameter(description = "아이디", example = "diggindie")
            @RequestParam String userId
    ) {

        Response<UserIdCheckResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "아이디 중복 확인 API",
                authService.checkExists(userId)
        );

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/auth/logout")
    public ResponseEntity<Response<LogoutResponse>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse httpResponse
    ) {

        Response<LogoutResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "로그아웃 API",
                authService.logout(httpResponse, userDetails.getExternalId(), userDetails.getUserId())
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token")
    })
    @PostMapping("/auth/reissue")
    public ResponseEntity<Response<TokenReissueResponse>> reissue(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        Response<TokenReissueResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "토큰 재발급 API",
                authService.reissue(httpRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

    // ===== 이메일 인증 API =====

    @Operation(summary = "회원가입 인증 코드 발송", description = "회원가입을 위한 이메일 인증 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "이미 사용 중인 이메일")
    })
    @PostMapping("/auth/email/signup/send")
    public ResponseEntity<Response<EmailVerificationResponse>> sendSignupVerificationCode(
            @Valid @RequestBody EmailVerificationRequest request
    ) {
        Response<EmailVerificationResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "회원가입 인증 코드 발송 API",
                authService.sendSignupVerificationCode(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "회원가입 인증 코드 확인", description = "회원가입 이메일 인증 코드를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 불일치 또는 만료")
    })
    @PostMapping("/auth/email/signup/verify")
    public ResponseEntity<Response<EmailVerificationResponse>> verifySignupCode(
            @Valid @RequestBody EmailVerificationConfirmRequest request
    ) {
        Response<EmailVerificationResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "회원가입 인증 코드 확인 API",
                authService.verifySignupCode(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 재설정 인증 코드 발송", description = "비밀번호 재설정을 위한 이메일 인증 코드를 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 발송 성공"),
            @ApiResponse(responseCode = "400", description = "등록되지 않은 이메일")
    })
    @PostMapping("/auth/email/password/send")
    public ResponseEntity<Response<EmailVerificationResponse>> sendPasswordResetCode(
            @Valid @RequestBody EmailVerificationRequest request
    ) {
        Response<EmailVerificationResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "비밀번호 재설정 인증 코드 발송 API",
                authService.sendPasswordResetCode(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 재설정 인증 코드 확인", description = "비밀번호 재설정 이메일 인증 코드를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "인증 코드 불일치 또는 만료")
    })
    @PostMapping("/auth/email/password/verify")
    public ResponseEntity<Response<EmailVerificationResponse>> verifyPasswordResetCode(
            @Valid @RequestBody EmailVerificationConfirmRequest request
    ) {
        Response<EmailVerificationResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "비밀번호 재설정 인증 코드 확인 API",
                authService.verifyPasswordResetCode(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 재설정", description = "인증 완료 후 새 비밀번호를 설정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 인증 필요 또는 등록되지 않은 이메일")
    })
    @PostMapping("/auth/password/reset")
    public ResponseEntity<Response<EmailVerificationResponse>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        Response<EmailVerificationResponse> response = Response.of(
                SuccessCode.UPDATE_SUCCESS,
                true,
                "비밀번호 재설정 API",
                authService.resetPassword(request)
        );
        return ResponseEntity.ok().body(response);
    }


}
