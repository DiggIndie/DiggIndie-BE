package ceos.diggindie.domain.spotify.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Spotify", description = "Spotify 관련 API (백엔드 내부용)")
@RestController
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @Operation(summary = "Spotify 토큰 발급 [내부용]", description = "Spotify API 사용을 위한 토큰을 발급받습니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/spotify/auth")
    public ResponseEntity<Response<?>> spotifyAuth() {

        Response<SpotifyTokenResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                spotifyService.getSpotifyToken()
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Spotify 검색 [내부용]", description = "Spotify에서 아티스트/트랙을 검색합니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/spotify/search")
    public ResponseEntity<Response<?>> spotifySearch(
            SpotifySearchRequest request) {
        Response<SpotifySearchResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                spotifyService.searchSpotify(request)
        );
        return ResponseEntity.ok().body(response);
    }

}
