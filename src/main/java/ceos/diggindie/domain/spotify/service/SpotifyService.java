package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.domain.spotify.dto.SpotifyAlbumsResponse;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTracksResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    // 토큰 캐싱
    private String cachedToken;
    private long tokenExpiryTime;

    public String getAccessToken() {
        // 토큰이 유효하면 캐시된 토큰 반환
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedToken;
        }

        SpotifyTokenResponse response = getSpotifyToken();
        if (response != null && response.accessToken() != null) {
            cachedToken = response.accessToken();
            // 만료 1분 전에 갱신하도록 설정
            tokenExpiryTime = System.currentTimeMillis() + (response.expiresIn() - 60) * 1000L;
        }
        return cachedToken;
    }

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

        String accessToken = getAccessToken();
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

    /**
     * 아티스트의 앨범 목록 조회
     */
    public SpotifyAlbumsResponse getArtistAlbums(String artistId, int limit) {

        String accessToken = getAccessToken();

        SpotifyAlbumsResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/artists/{artistId}/albums")
                        .queryParam("include_groups", "album,single")
                        .queryParam("market", "KR")
                        .queryParam("limit", limit)
                        .build(artistId))
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyAlbumsResponse.class);

        if (response == null) {
            log.warn("아티스트 앨범 조회 실패: {}", artistId);
        } else {
            log.info("아티스트 앨범 조회 성공: {} ({}개)", artistId, response.items().size());
        }

        return response;
    }

    /**
     * 앨범의 트랙 목록 조회
     */
    public SpotifyTracksResponse getAlbumTracks(String albumId) {

        String accessToken = getAccessToken();

        SpotifyTracksResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/albums/{albumId}/tracks")
                        .queryParam("market", "KR")
                        .queryParam("limit", 50)
                        .build(albumId))
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyTracksResponse.class);

        if (response == null) {
            log.warn("앨범 트랙 조회 실패: {}", albumId);
        } else {
            log.info("앨범 트랙 조회 성공: {} ({}곡)", albumId, response.items().size());
        }

        return response;
    }
}