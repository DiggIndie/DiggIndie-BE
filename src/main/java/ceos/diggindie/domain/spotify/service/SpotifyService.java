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

         ResponseEntity<SpotifyTokenResponse> response = restClient
                    .post()
                    .uri("https://accounts.spotify.com/api/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(payload)
                    .retrieve()
                    .toEntity(SpotifyTokenResponse.class);

        if (response == null || response.getBody().accessToken() == null) {
            log.info("Spotify access token 발급 실패");
        } else {
            log.info("Spotify access token 발급 성공, tokenType={}, expiresIn={}",
                    response.getBody().tokenType(), response.getBody().expiresIn());
        }

        return response.getBody();
    }



    public SpotifySearchResponse searchSpotify(SpotifySearchRequest request) {

        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        String accessToken = tokenResponse.accessToken();
        String query = request.query();

        ResponseEntity<SpotifySearchResponse> response = restClient
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
                .toEntity(SpotifySearchResponse.class);

        if (response == null || response.getBody() == null) {
            log.info("Spotify 검색 실패");
        } else {
            log.info("Spotify 검색 성공, artists={}", response.getBody().artists().items());
        }

        return response.getBody();
    }

    public void updateSpotifyInfo() {

        // query는 DB에서 가져와야 함
        String query = "실리카겔";
        SpotifySearchRequest request = new SpotifySearchRequest(query);

        SpotifySearchResponse response = searchSpotify(request);

        response.artists().items().forEach(artist -> {

            String spotifyId = artist.id();
            String imageUrl = artist.images().isEmpty() ? "" : artist.images().get(0).url();

            // DB에 Spotify ID 업데이트 로직 작성 필요
            log.info("검색된 Spotify ID: {}, 이미지 URL: {}", spotifyId, imageUrl);
        });
    }


}
