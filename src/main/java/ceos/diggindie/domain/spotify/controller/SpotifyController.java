package ceos.diggindie.domain.spotify.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

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
