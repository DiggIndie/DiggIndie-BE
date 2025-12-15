package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client-id}")
    private String CLIENT_ID;

    @Value("${spotify.client-secret}")
    private String CLIENT_SECRET;

    private final RestClient restClient;

    public SpotifyTokenResponse getSpotifyToken() {

         MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
         payload.add("grant_type", "client_credentials");
         payload.add("client_id", CLIENT_ID);
         payload.add("client_secret", CLIENT_SECRET);

         SpotifyTokenResponse response = restClient
                    .post()
                    .uri("https://accounts.spotify.com/api/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(payload)
                    .retrieve()
                    .body(SpotifyTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            log.info("Spotify access token 발급 실패");
        } else {
            log.info("Spotify access token 발급 성공");
        }

        return response;
    }



    public SpotifySearchResponse searchSpotify(SpotifySearchRequest request) {

        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        String accessToken = tokenResponse.accessToken();
        String query = request.query();

        SpotifySearchResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/search")
                        .queryParam("q", query)
                        .queryParam("type", "artist")
                        .queryParam("market", "KR")
                        .queryParam("limit", 1)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifySearchResponse.class);

        if (response == null) {
            log.info("Spotify 검색 실패");
        } else {
            log.info("Spotify 검색 성공");
        }

        return response;
    }
}
