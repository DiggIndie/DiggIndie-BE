package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.*;
import ceos.diggindie.domain.member.dto.auth.*;
import ceos.diggindie.domain.member.dto.oauth.*;
import ceos.diggindie.domain.member.service.AuthService;
import ceos.diggindie.domain.member.service.EmailService;
import ceos.diggindie.domain.member.service.MemberService;
import ceos.diggindie.domain.member.service.OAuth2Service;
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
    private final OAuth2Service oAuth2Service;
    private final MemberService memberService;
    private final EmailService emailService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디")
    })
    @PostMapping("/auth/signup")
    public ResponseEntity<Response<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest,
            HttpServletResponse httpResponse
    ) {
        SignupResponse signupResponse = authService.signup(signupRequest, httpResponse);
        Response<SignupResponse> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                signupResponse,
                "회원 가입 API"
        );
        return ResponseEntity.status(201).body(response);
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
        LoginResponse loginResponse = authService.login(loginRequest, httpResponse);
        Response<LoginResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                loginResponse,
                "일반 로그인 API"
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
        UserIdCheckResponse userIdCheckResponse = authService.checkExists(userId);
        Response<UserIdCheckResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                userIdCheckResponse,
                "아이디 중복 확인 API"
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
        LogoutResponse logoutResponse = authService.logout(httpResponse, userDetails.getExternalId(), userDetails.getUserId());
        Response<LogoutResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                logoutResponse,
                "로그아웃 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @PostMapping("/auth/reissue")
    public ResponseEntity<Response<TokenReissueResponse>> reissue(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        TokenReissueResponse tokenReissueResponse = authService.reissue(httpRequest, httpResponse);
        Response<TokenReissueResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                tokenReissueResponse,
                "토큰 재발급 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "OAuth2 인증 URL 조회", description = "소셜 로그인/연동을 위한 인증 URL을 반환합니다.")
    @GetMapping("/auth/oauth2/url/{platform}")
    public ResponseEntity<Response<OAuth2UrlResponse>> getOAuth2AuthUrl(
            @PathVariable LoginPlatform platform,
            @Parameter(description = "목적 (login 또는 link)", example = "login")
            @RequestParam(defaultValue = "login") String purpose
    ) {
        Response<OAuth2UrlResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                oAuth2Service.getAuthUrl(platform, purpose),
                "OAuth2 인증 URL 조회 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "OAuth2 통합 콜백", description = "로그인/연동을 state 기반으로 자동 분기 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "처리 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 state"),
            @ApiResponse(responseCode = "401", description = "연동 시 인증되지 않은 사용자")
    })
    @PostMapping("/auth/oauth2/callback")
    public ResponseEntity<Response<OAuth2CallbackResponse>> handleCallback(
            @RequestBody OAuth2CallbackRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse httpResponse
    ) {
        Response<OAuth2CallbackResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                oAuth2Service.handleCallback(request, userDetails, httpResponse),
                "OAuth2 통합 콜백 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "소셜 계정 연동 해제", description = "연동된 소셜 계정을 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연동 해제 성공"),
            @ApiResponse(responseCode = "400", description = "마지막 로그인 수단은 해제 불가"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "연동되지 않은 계정")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/auth/oauth2/unlink/{platform}")
    public ResponseEntity<Response<OAuth2UnlinkResponse>> unlinkSocialAccount(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable LoginPlatform platform
    ) {
        Response<OAuth2UnlinkResponse> response = Response.success(
                SuccessCode.DELETE_SUCCESS,
                oAuth2Service.unlinkSocialAccount(userDetails, platform),
                "소셜 계정 연동 해제 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "연동된 소셜 계정 목록 조회", description = "현재 회원에게 연동된 소셜 계정 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/auth/oauth2/accounts")
    public ResponseEntity<Response<LinkedSocialAccountResponse>> getLinkedAccounts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Response<LinkedSocialAccountResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                oAuth2Service.getLinkedAccounts(userDetails),
                "연동된 소셜 계정 목록 조회 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "사용자 아이디 조회", description = "현재 로그인한 유저의 memberId와 userId를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/user-id")
    public ResponseEntity<Response<MemberIdResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Response<MemberIdResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                authService.getCurrentUser(userDetails.getExternalId()),
                "사용자 아이디 조회 API"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "마케팅 동의 변경", description = "로그인한 사용자의 마케팅 동의를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마케팅 동의 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/my/marketing-consent")
    public ResponseEntity<Response<MarketingConsentResponse>> updateMarketingConsent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MarketingConsentRequest request
    ) {
        Response<MarketingConsentResponse> response = Response.success(
                SuccessCode.UPDATE_SUCCESS,
                memberService.updateMarketingConsent(userDetails.getExternalId(), request),
                "마케팅 동의 변경 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "마케팅 동의 조회", description = "로그인한 사용자의 마케팅 동의 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/marketing-consent")
    public ResponseEntity<Response<MarketingConsentResponse>> getMarketingConsent(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Response<MarketingConsentResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                memberService.getMarketingConsent(userDetails.getExternalId()),
                "마케팅 동의 조회 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = "이메일 인증 후 발급받은 resetToken으로 비밀번호를 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰 또는 비밀번호 형식 오류"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일")
    })
    @PostMapping("/auth/password/reset")
    public ResponseEntity<Response<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        emailService.resetPassword(request);
        return ResponseEntity.ok(
                Response.success(SuccessCode.UPDATE_SUCCESS, "비밀번호가 변경되었습니다.")
        );
    }
}