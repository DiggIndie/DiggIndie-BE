package ceos.diggindie.domain.spotify.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.dto.TopTrackUpdateAllResponse;
import ceos.diggindie.domain.spotify.dto.TopTrackUpdateResponse;
import ceos.diggindie.domain.spotify.service.SpotifyImportService;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Spotify", description = "Spotify 관련 API (백엔드 내부용)")
@RestController
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final SpotifyImportService spotifyImportService;

    @Operation(summary = "Spotify 토큰 발급 [내부용]", description = "Spotify API 사용을 위한 토큰을 발급받습니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/spotify/auth")
    public ResponseEntity<Response<?>> spotifyAuth() {
        Response<SpotifyTokenResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
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
    public ResponseEntity<Response<?>> spotifySearch(SpotifySearchRequest request) {
        Response<SpotifySearchResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                spotifyService.searchSpotify(request)
        );
        return ResponseEntity.ok().body(response);
    }

    // 전체 밴드 앨범/곡 Import
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Spotify 전체 밴드 앨범/곡 Import [내부용]", description = "전체 밴드의 앨범/곡을 Spotify에서 가져옵니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Import 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PostMapping("/api/admin/spotify/import-albums")
    public ResponseEntity<Response<String>> importAllAlbums() {

        spotifyImportService.importAllBandsAlbums();
        Response<String> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                "앨범/곡 Import 완료"
        );

        return ResponseEntity.status(201).body(response);
    }

    // 특정 밴드 앨범/곡 Import (bandId로)
    @PreAuthorize("hasRoles('ADMIN')")
    @Operation(summary = "Spotify 특정 밴드 앨범/곡 Import [내부용]", description = "bandId로 특정 밴드의 앨범/곡을 Spotify에서 가져옵니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Import 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PostMapping("/api/admin/spotify/import-albums/{bandId}")
    public ResponseEntity<Response<String>> importBandAlbums(@PathVariable Long bandId) {

        Response<String> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                "밴드 앨범/곡 Import 완료"
        );

        return ResponseEntity.ok().body(response);
    }

    // 특정 밴드 대표곡 업데이트 (bandId로)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Spotify 특정 밴드 대표곡 업데이트 [내부용]", description = "bandId로 특정 밴드의 대표곡을 Spotify에서 가져옵니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PostMapping("/api/admin/spotify/update/top-track/{bandId}")
    public ResponseEntity<Response<TopTrackUpdateResponse>> updateTopTrack(@PathVariable Long bandId) {
        
        TopTrackUpdateResponse result = spotifyService.updateTopTrackByBandId(bandId);
        Response<TopTrackUpdateResponse> response = Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "개별 밴드 대표곡 업데이트 성공"
        );
        return ResponseEntity.ok().body(response);
    }

    // 전체 밴드 대표곡 일괄 업데이트
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Spotify 전체 밴드 대표곡 일괄 업데이트 [내부용]", description = "전체 밴드의 대표곡을 Spotify에서 가져옵니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PostMapping("/api/admin/spotify/update/top-track/all")
    public ResponseEntity<Response<TopTrackUpdateAllResponse>> updateAllTopTracks() {
        
        TopTrackUpdateAllResponse result = spotifyService.updateAllTopTrack();
        Response<TopTrackUpdateAllResponse> response = Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "전체 밴드 대표곡 업데이트 성공"
        );

        return ResponseEntity.ok().body(response);
    }
}