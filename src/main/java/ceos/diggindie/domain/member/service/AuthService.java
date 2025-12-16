package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.config.security.jwt.JwtTokenProvider;
import ceos.diggindie.common.enums.Role;
import ceos.diggindie.domain.member.dto.*;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        Member member = memberRepository.findByUserId(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setCookies(response, member.getExternalId(), member.getRole());

        return new LoginResponse(member.getExternalId(), accessToken, accessTokenValidity.getSeconds(), member.getUserId(), false);
    }

    @Transactional
    public SignupResponse signup(SignupRequest request, HttpServletResponse response) {

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .userId(request.userId())
                .password(encodedPassword)
                .email(request.email())
                .build();

        Member savedMember = memberRepository.save(member);

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setCookies(response, savedMember.getExternalId(), savedMember.getRole());

        return new SignupResponse(savedMember.getExternalId(), accessToken, accessTokenValidity.getSeconds(), savedMember.getUserId(), true);
    }

    private void setCookies(HttpServletResponse response, String externalId, Role role) {

        String refreshToken = jwtTokenProvider.generateRefreshToken(externalId, role);
        addTokenCookie(response, "refreshToken", refreshToken, refreshTokenValidity);

        // refresh token 저장 로직 추가 예정

    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public UserIdCheckResponse checkExists(String userId) {
        boolean isAvailable = !memberRepository.existsByUserId(userId);
        return new UserIdCheckResponse(isAvailable);
    }

    public LogoutResponse logout(HttpServletResponse response, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        removeRefreshTokenCookie(response);

        return new LogoutResponse(member.getExternalId(), member.getUserId());
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
