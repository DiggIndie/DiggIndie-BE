package ceos.diggindie.domain.spotify.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.service.SpotifyImportService;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;
    private final SpotifyImportService spotifyImportService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/spotify/search")
    public ResponseEntity<Response<?>> spotifySearch(SpotifySearchRequest request) {
        Response<SpotifySearchResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                spotifyService.searchSpotify(request)
        );
        return ResponseEntity.ok().body(response);
    }

    // 전체 밴드 앨범/곡 Import
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/spotify/import-albums")
    public ResponseEntity<Response<String>> importAllAlbums() {
        spotifyImportService.importAllBandsAlbums();
        return ResponseEntity.ok().body(
                Response.of(SuccessCode.INSERT_SUCCESS, true, "앨범/곡 Import 완료")
        );
    }

    // 특정 밴드 앨범/곡 Import (bandId로)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/spotify/import-albums/{bandId}")
    public ResponseEntity<Response<String>> importBandAlbums(@PathVariable Long bandId) {
        return ResponseEntity.ok().body(
                Response.of(SuccessCode.INSERT_SUCCESS, true, "밴드 앨범/곡 Import 완료")
        );
    }
}