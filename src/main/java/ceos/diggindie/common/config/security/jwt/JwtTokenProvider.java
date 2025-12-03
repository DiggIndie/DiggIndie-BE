package ceos.diggindie.common.config.security.jwt;

import ceos.diggindie.common.config.security.CustomUserDetailService;
import ceos.diggindie.common.config.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    private Key key;

    private final CustomUserDetailService customUserDetailService;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String externalId) {
        return generateToken(externalId, accessTokenValidity);
    }

    public String generateRefreshToken(String externalId) {
        return generateToken(externalId, refreshTokenValidity);
    }

    public String generateToken(String externalId, Duration expiration) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .setSubject(externalId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getAccessToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims parseClaims(String token) {

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey)key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;
    }

    public String getExternalId(String token) {
        String externalId = parseClaims(token).getSubject();
        return externalId;
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 파싱과 동시에 검증 수행
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 서명 또는 JWT 형식
             log.warn("JWT 토큰 형식이 잘못되었습니다.", e);
        } catch (ExpiredJwtException e) {
            // 만료된 JWT
             log.warn("만료된 JWT 토큰이 사용되었습니다.", e);
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 JWT
             log.warn("지원하지 않는 JWT 토큰이 사용되었습니다.", e);
        } catch (IllegalArgumentException e) {
            // 빈 JWT 또는 기타 문제
             log.warn("JWT 토큰의 값이 비어있습니다.", e);
        }
        return false;
    }

    public Authentication getAuthentication(String token) {

        String externalId = getExternalId(token);
        CustomUserDetails userDetails = customUserDetailService.loadByExternalId(externalId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}
