package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.*;
import ceos.diggindie.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

    @GetMapping("/auth/exists")
    public ResponseEntity<Response<UserIdCheckResponse>> checkExists(
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

}
