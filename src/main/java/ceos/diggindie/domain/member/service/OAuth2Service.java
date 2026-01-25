package ceos.diggindie.domain.member.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.common.config.oauth.OAuth2Client;
import ceos.diggindie.common.config.oauth.OAuth2Properties;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.config.security.jwt.JwtTokenProvider;
import ceos.diggindie.common.enums.LoginPlatform;
import ceos.diggindie.common.enums.Role;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.common.exception.GeneralException;
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
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final OAuth2StateService oAuth2StateService;
    private final TransactionTemplate transactionTemplate;

    @Value("${jwt.refresh-token-validity}")
    private java.time.Duration refreshTokenValidity;

    /**
     * 소셜 로그인 (회원가입 포함)
     */
    public OAuth2LoginResponse login(OAuth2LoginRequest request, HttpServletResponse response) {
        validateState(request.getState(), request.getPlatform());

        OAuth2UserInfo userInfo = oAuth2Client.getUserInfo(request.getPlatform(), request.getCode());

        LoginResult result = transactionTemplate.execute(status -> {
            Optional<SocialAccount> existingSocialAccount = socialAccountRepository
                    .findByPlatformAndPlatformIdWithMember(userInfo.platform(), userInfo.platformId());

            Member member;
            boolean isNewMember = false;

            if (existingSocialAccount.isPresent()) {
                member = existingSocialAccount.get().getMember();
                member.updateRecentLoginPlatform(userInfo.platform());

                if (userInfo.email() != null) {
                    existingSocialAccount.get().updateEmail(userInfo.email());
                }
            } else {
                member = createNewMember(userInfo);
                isNewMember = true;
            }

            return new LoginResult(member, isNewMember);
        });

        Member member = result.member();
        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setRefreshToken(response, member.getExternalId(), member.getRole());

        return OAuth2LoginResponse.builder()
                .newMember(result.isNewMember())
                .externalId(member.getExternalId())
                .userId(member.getUserId())
                .email(member.getEmail())
                .platform(userInfo.platform())
                .accessToken(accessToken)
                .build();
    }

    /**
     * 기존 계정에 소셜 계정 연동
     */
    public OAuth2LinkResponse linkSocialAccount(CustomUserDetails userDetails, OAuth2LinkRequest request) {
        validateState(request.getState(), request.getPlatform());
        Long memberId = userDetails.getMemberId();

        transactionTemplate.executeWithoutResult(status -> {
            if (socialAccountRepository.existsByMemberIdAndPlatform(memberId, request.getPlatform())) {
                throw new BusinessException(BusinessErrorCode.OAUTH_ALREADY_LINKED);
            }
        });

        OAuth2UserInfo userInfo = oAuth2Client.getUserInfo(request.getPlatform(), request.getCode());

        transactionTemplate.executeWithoutResult(status -> {
            if (socialAccountRepository.findByPlatformAndPlatformId(
                    userInfo.platform(), userInfo.platformId()).isPresent()) {
                throw new BusinessException(BusinessErrorCode.OAUTH_ACCOUNT_EXISTS);
            }

            Member memberRef = memberRepository.getReferenceById(memberId);

            SocialAccount socialAccount = SocialAccount.builder()
                    .platform(userInfo.platform())
                    .platformId(userInfo.platformId())
                    .email(userInfo.email())
                    .member(memberRef)
                    .build();

            socialAccountRepository.save(socialAccount);
        });

        return OAuth2LinkResponse.builder()
                .success(true)
                .platform(userInfo.platform())
                .email(userInfo.email())
                .message(userInfo.platform().getDescription() + " 계정이 연동되었습니다.")
                .build();
    }

    /**
     * 소셜 계정 연동 해제
     */
    @Transactional
    public OAuth2UnlinkResponse unlinkSocialAccount(CustomUserDetails userDetails, LoginPlatform platform) {
        if (platform == LoginPlatform.LOCAL) {
            throw new BusinessException(BusinessErrorCode.OAUTH_INVALID_PLATFORM, "기본 회원가입은 연동 해제할 수 없습니다.");
        }

        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));

        long linkedCount = socialAccountRepository.countByMemberId(member.getId());
        boolean hasPassword = member.getPassword() != null;

        if (linkedCount <= 1 && !hasPassword) {
            throw new BusinessException(BusinessErrorCode.OAUTH_UNLINK_DENIED);
        }

        if (!socialAccountRepository.existsByMemberIdAndPlatform(member.getId(), platform)) {
            throw new BusinessException(BusinessErrorCode.OAUTH_NOT_LINKED);
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

        String state = oAuth2StateService.generateState(platform.name());

        String authUrl = switch (platform) {
            case KAKAO -> String.format(
                    "https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=profile_nickname,account_email&state=%s",
                    provider.getClientId(), provider.getRedirectUri(), state);
            case NAVER -> String.format(
                    "https://nid.naver.com/oauth2.0/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=%s",
                    provider.getClientId(), provider.getRedirectUri(), state);
            case GOOGLE -> String.format(
                    "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=profile%%20email&state=%s",
                    provider.getClientId(), provider.getRedirectUri(), state);
            default -> throw new BusinessException(BusinessErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED);
        };

        return OAuth2UrlResponse.builder()
                .authUrl(authUrl)
                .state(state)
                .build();
    }

    private Member createNewMember(OAuth2UserInfo userInfo) {
        Member member = Member.createSocialMember(userInfo.email(), userInfo.platform());
        memberRepository.save(member);

        SocialAccount socialAccount = SocialAccount.builder()
                .platform(userInfo.platform())
                .platformId(userInfo.platformId())
                .email(userInfo.email())
                .member(member)
                .build();

        socialAccountRepository.save(socialAccount);
        member.addSocialAccount(socialAccount);

        return member;
    }

    private void setRefreshToken(HttpServletResponse response, String externalId, Role role) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(externalId, role);
        refreshTokenService.save(externalId, refreshToken);
        addTokenCookie(response, "refresh_token", refreshToken, refreshTokenValidity);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, java.time.Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void validateState(String state, LoginPlatform platform) {
        if (!oAuth2StateService.validateAndConsume(state, platform.name())) {
            log.warn("OAuth state validation failed - state: {}, platform: {}", state, platform);
            throw new BusinessException(BusinessErrorCode.OAUTH_INVALID_STATE);
        }
    }

    // 내부용 record
    private record LoginResult(Member member, boolean isNewMember) {}
}