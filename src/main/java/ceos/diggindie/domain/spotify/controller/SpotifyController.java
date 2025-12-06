package ceos.diggindie.domain.spotify.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/spotify/auth")
    public ResponseEntity<Response<?>> spotifyAuth() {

        Response<SpotifyTokenResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                spotifyService.getSpotifyToken()
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/spotify/search")
    public ResponseEntity<Response<?>> spotifySearch(
            SpotifySearchRequest request) {
        Response<SpotifySearchResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                spotifyService.searchSpotify(request)
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/spotify/update")
    public ResponseEntity<Response<?>> spotifyUpdate() {
        spotifyService.updateSpotifyInfo();
        Response<Void> response = Response.of(
                SuccessCode.UPDATE_SUCCESS,
                true,
                null
        );
        return ResponseEntity.ok().body(response);
    }
}
