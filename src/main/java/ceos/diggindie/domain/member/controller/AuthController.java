package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.LoginRequest;
import ceos.diggindie.domain.member.dto.LoginResponse;
import ceos.diggindie.domain.member.dto.SignupRequest;
import ceos.diggindie.domain.member.dto.SignupResponse;
import ceos.diggindie.domain.member.dto.UserIdCheckResponse;
import ceos.diggindie.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/users/signup")
    public ResponseEntity<Response<SignupResponse>> signup(
            @RequestBody SignupRequest signupRequest,
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

}
