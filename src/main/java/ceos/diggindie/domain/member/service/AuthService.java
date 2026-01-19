package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.config.security.jwt.JwtTokenProvider;
import ceos.diggindie.common.enums.Role;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.member.dto.*;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    @Transactional(readOnly = true)
    public Member findMemberByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_CREDENTIALS));
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Member member = findMemberByUserId(request.userId());

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(BusinessErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setRefreshToken(response, member.getExternalId(), member.getRole());

        return new LoginResponse(member.getExternalId(), accessToken, accessTokenValidity.getSeconds(), member.getUserId(), false);
    }

    @Transactional
    public Member createMember(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(BusinessErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .userId(request.userId())
                .password(encodedPassword)
                .email(request.email())
                .build();

        return memberRepository.save(member);
    }

    public SignupResponse signup(SignupRequest request, HttpServletResponse response) {
        Member savedMember = createMember(request);

        String accessToken = jwtTokenProvider.generateAccessToken(savedMember.getExternalId(), savedMember.getRole());
        setRefreshToken(response, savedMember.getExternalId(), savedMember.getRole());

        return new SignupResponse(savedMember.getExternalId(), accessToken, accessTokenValidity.getSeconds(), savedMember.getUserId(), true);
    }

    private void setRefreshToken(HttpServletResponse response, String externalId, Role role) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(externalId, role);
        refreshTokenService.save(externalId, refreshToken);
        addTokenCookie(response, "refreshToken", refreshToken, refreshTokenValidity);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public UserIdCheckResponse checkExists(String userId) {
        boolean isAvailable = !memberRepository.existsByUserId(userId);
        return new UserIdCheckResponse(isAvailable);
    }

    @Transactional(readOnly = true)
    public LogoutResponse logout(HttpServletResponse response, String externalId, String userId) {
        refreshTokenService.delete(externalId);
        removeRefreshTokenCookie(response);
        return new LogoutResponse(externalId, userId);
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

    @Transactional(readOnly = true)
    public TokenReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new BusinessException(BusinessErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String externalId = jwtTokenProvider.parseClaims(refreshToken).getSubject();

        if (!refreshTokenService.validate(externalId, refreshToken)) {
            refreshTokenService.delete(externalId);
            removeRefreshTokenCookie(response);
            throw new BusinessException(BusinessErrorCode.REFRESH_TOKEN_INVALID);
        }

        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateAccessToken(externalId, member.getRole());
        setRefreshToken(response, externalId, member.getRole());

        return new TokenReissueResponse(newAccessToken, accessTokenValidity.getSeconds());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}