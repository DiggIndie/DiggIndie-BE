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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token")
    })
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

}
