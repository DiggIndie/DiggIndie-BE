package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.config.oauth.OAuth2Client;
import ceos.diggindie.common.config.oauth.OAuth2Properties;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.config.security.jwt.JwtTokenProvider;
import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.status.ErrorStatus;
import ceos.diggindie.domain.member.dto.oauth.*;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.entity.SocialAccount;
import ceos.diggindie.domain.member.repository.MemberRepository;
import ceos.diggindie.domain.member.repository.SocialAccountRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuth2Client oAuth2Client;
    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2Properties oAuth2Properties;

    @Value("${jwt.refresh-token-validity}")
    private java.time.Duration refreshTokenValidity;

    /**
     * 소셜 로그인 (회원가입 포함)
     */
    @Transactional
    public OAuth2LoginResponse login(OAuth2LoginRequest request, HttpServletResponse response) {
        OAuth2UserInfo userInfo = oAuth2Client.getUserInfo(request.getPlatform(), request.getCode());

        Optional<SocialAccount> existingSocialAccount = socialAccountRepository
                .findByPlatformAndPlatformIdWithMember(userInfo.getPlatform(), userInfo.getPlatformId());

        Member member;
        boolean isNewMember = false;

        if (existingSocialAccount.isPresent()) {
            member = existingSocialAccount.get().getMember();
            member.updateRecentLoginPlatform(userInfo.getPlatform());

            if (userInfo.getEmail() != null) {
                existingSocialAccount.get().updateEmail(userInfo.getEmail());
            }
        } else {
            member = createNewMember(userInfo);
            isNewMember = true;
        }

        // 메서드명 수정 + Role 파라미터 추가
        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getExternalId(), member.getRole());

        refreshTokenService.save(member.getExternalId(), refreshToken);

        // 쿠키 직접 설정
        setRefreshTokenCookie(response, refreshToken);

        return OAuth2LoginResponse.builder()
                .newMember(isNewMember)
                .memberId(member.getId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .platform(userInfo.getPlatform())
                .accessToken(accessToken)
                .build();
    }

    /**
     * 기존 계정에 소셜 계정 연동
     */
    @Transactional
    public OAuth2LinkResponse linkSocialAccount(CustomUserDetails userDetails, OAuth2LinkRequest request) {
        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "회원을 찾을 수 없습니다."));

        if (socialAccountRepository.existsByMemberIdAndPlatform(member.getId(), request.getPlatform())) {
            throw new GeneralException(ErrorStatus.OAUTH_ALREADY_LINKED);
        }

        OAuth2UserInfo userInfo = oAuth2Client.getUserInfo(request.getPlatform(), request.getCode());

        if (socialAccountRepository.findByPlatformAndPlatformId(
                userInfo.getPlatform(), userInfo.getPlatformId()).isPresent()) {
            throw new GeneralException(ErrorStatus.OAUTH_ACCOUNT_EXISTS);
        }

        SocialAccount socialAccount = SocialAccount.builder()
                .platform(userInfo.getPlatform())
                .platformId(userInfo.getPlatformId())
                .email(userInfo.getEmail())
                .member(member)
                .build();

        socialAccountRepository.save(socialAccount);
        member.addSocialAccount(socialAccount);

        return OAuth2LinkResponse.builder()
                .success(true)
                .platform(userInfo.getPlatform())
                .email(userInfo.getEmail())
                .message(userInfo.getPlatform().getDescription() + " 계정이 연동되었습니다.")
                .build();
    }

    /**
     * 소셜 계정 연동 해제
     */
    @Transactional
    public OAuth2UnlinkResponse unlinkSocialAccount(CustomUserDetails userDetails, LoginPlatform platform) {
        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND, "회원을 찾을 수 없습니다."));

        long linkedCount = socialAccountRepository.countByMemberId(member.getId());
        boolean hasPassword = member.getPassword() != null;

        if (linkedCount <= 1 && !hasPassword) {
            throw new GeneralException(ErrorStatus.OAUTH_UNLINK_DENIED);
        }

        if (!socialAccountRepository.existsByMemberIdAndPlatform(member.getId(), platform)) {
            throw new GeneralException(ErrorStatus.OAUTH_NOT_LINKED);
        }

        socialAccountRepository.deleteByMemberIdAndPlatform(member.getId(), platform);

        return OAuth2UnlinkResponse.builder()
                .success(true)
                .platform(platform)
                .message(platform.getDescription() + " 계정 연동이 해제되었습니다.")
                .build();
    }

    /**
     * 연동된 소셜 계정 목록 조회
     */
    @Transactional(readOnly = true)
    public LinkedSocialAccountResponse getLinkedAccounts(CustomUserDetails userDetails) {
        List<SocialAccount> socialAccounts = socialAccountRepository
                .findAllByMemberId(userDetails.getMemberId());

        List<LinkedSocialAccountResponse.SocialAccountInfo> accountInfos = socialAccounts.stream()
                .map(sa -> LinkedSocialAccountResponse.SocialAccountInfo.builder()
                        .platform(sa.getPlatform())
                        .email(sa.getEmail())
                        .connectedAt(sa.getCreatedAt())
                        .build())
                .toList();

        return LinkedSocialAccountResponse.builder()
                .accounts(accountInfos)
                .build();
    }

    /**
     * OAuth2 인증 URL 조회
     */
    public OAuth2UrlResponse getAuthUrl(LoginPlatform platform) {
        OAuth2Properties.Provider provider = oAuth2Properties.getProvider(platform.name());

        String authUrl = switch (platform) {
            case KAKAO -> String.format(
                    "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=profile_nickname,account_email",
                    provider.getClientId(), provider.getRedirectUri());
            case NAVER -> String.format(
                    "https://nid.naver.com/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=STATE",
                    provider.getClientId(), provider.getRedirectUri());
            case GOOGLE -> String.format(
                    "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=profile%%20email",
                    provider.getClientId(), provider.getRedirectUri());
            default -> throw new GeneralException(ErrorStatus.OAUTH_PROVIDER_NOT_SUPPORTED);
        };

        return OAuth2UrlResponse.builder()
                .authUrl(authUrl)
                .build();
    }

    private Member createNewMember(OAuth2UserInfo userInfo) {
        Member member = Member.createSocialMember(userInfo.getEmail(), userInfo.getPlatform());
        memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .platform(userInfo.getPlatform())
                .platformId(userInfo.getPlatformId())
                .email(userInfo.getEmail())
                .member(member)
                .build();

        socialAccountRepository.save(socialAccount);
        member.addSocialAccount(socialAccount);

        return member;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) refreshTokenValidity.toSeconds());
        response.addCookie(cookie);
    }
}