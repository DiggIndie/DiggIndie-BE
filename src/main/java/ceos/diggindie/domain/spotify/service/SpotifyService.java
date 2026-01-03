package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.TopTrack;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.band.repository.TopTrackRepository;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTokenResponse;
import ceos.diggindie.domain.spotify.dto.SpotifyTopTrackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    @Value("${spotify.client-id}")
    private String CLIENT_ID;

    @Value("${spotify.client-secret}")
    private String CLIENT_SECRET;

    private final RestClient restClient;
    private final BandRepository bandRepository;
    private final TopTrackRepository topTrackRepository;

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

    public SpotifyTopTrackResponse getTopTracks(String spotifyId, String accessToken) {

        SpotifyTopTrackResponse response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.spotify.com")
                        .path("/v1/artists/{id}/top-tracks")
                        .queryParam("market", "KR")
                        .build(spotifyId))
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(SpotifyTopTrackResponse.class);

        if (response == null || response.tracks() == null || response.tracks().isEmpty()) {
            log.info("Spotify Top Tracks 조회 실패 - spotifyId: {}", spotifyId);
            return null;
        }

        log.info("Spotify Top Tracks 조회 성공 - spotifyId: {}", spotifyId);
        return response;
    }

    @Transactional
    public int syncAllTopTracks() {
        // 모든 밴드 조회
        List<Band> bands = bandRepository.findAll();

        // Spotify access token 발급
        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            log.error("Spotify access token 발급 실패");
            return 0;
        }
        String accessToken = tokenResponse.accessToken();

        int successCount = 0;

        for (Band band : bands) {
            String spotifyId = band.getSpotifyId();

            // spotifyId가 없는 밴드는 스킵
            if (spotifyId == null || spotifyId.isBlank()) {
                log.info("Spotify ID가 없는 밴드 스킵 - bandId: {}, bandName: {}", band.getId(), band.getBandName());
                continue;
            }

            try {
                // 밴드의 스포티파이 ID로 top track 조회
                SpotifyTopTrackResponse topTracksResponse = getTopTracks(spotifyId, accessToken);

                if (topTracksResponse == null || topTracksResponse.tracks().isEmpty()) {
                    log.info("Top Track이 없는 밴드 스킵 - bandId: {}, bandName: {}", band.getId(), band.getBandName());
                    continue;
                }

                // 첫 번째 트랙(가장 인기 있는 트랙)을 대표곡으로 저장
                SpotifyTopTrackResponse.TrackDto topTrack = topTracksResponse.tracks().get(0);
                String title = topTrack.name();
                String externalUrl = topTrack.external_urls().spotify();

                // 기존 TopTrack이 있는지 확인
                Optional<TopTrack> existingTopTrack = topTrackRepository.findByBand(band);

                if (existingTopTrack.isPresent()) {
                    // 기존 TopTrack 업데이트
                    existingTopTrack.get().update(title, externalUrl);
                    log.info("TopTrack 업데이트 완료 - bandName: {}, title: {}", band.getBandName(), title);
                } else {
                    // 새로운 TopTrack 생성
                    TopTrack newTopTrack = TopTrack.builder()
                            .band(band)
                            .title(title)
                            .externalUrl(externalUrl)
                            .build();
                    topTrackRepository.save(newTopTrack);
                    log.info("TopTrack 저장 완료 - bandName: {}, title: {}", band.getBandName(), title);
                }

                successCount++;

            } catch (Exception e) {
                log.error("TopTrack 저장 실패 - bandId: {}, bandName: {}, error: {}",
                        band.getId(), band.getBandName(), e.getMessage());
            }
        }

        log.info("TopTrack 동기화 완료 - 총 {}개 밴드 중 {}개 성공", bands.size(), successCount);
        return successCount;
    }

    @Transactional
    public boolean syncTopTrackByBandId(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 밴드입니다. bandId: " + bandId));

        String spotifyId = band.getSpotifyId();
        if (spotifyId == null || spotifyId.isBlank()) {
            log.error("Spotify ID가 없는 밴드입니다 - bandId: {}", bandId);
            return false;
        }

        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            log.error("Spotify access token 발급 실패");
            return false;
        }

        SpotifyTopTrackResponse topTracksResponse = getTopTracks(spotifyId, tokenResponse.accessToken());

        if (topTracksResponse == null || topTracksResponse.tracks().isEmpty()) {
            log.info("Top Track이 없는 밴드 - bandId: {}", bandId);
            return false;
        }

        SpotifyTopTrackResponse.TrackDto topTrack = topTracksResponse.tracks().get(0);
        String title = topTrack.name();
        String externalUrl = topTrack.external_urls().spotify();

        Optional<TopTrack> existingTopTrack = topTrackRepository.findByBand(band);

        if (existingTopTrack.isPresent()) {
            existingTopTrack.get().update(title, externalUrl);
            log.info("TopTrack 업데이트 완료 - bandName: {}, title: {}", band.getBandName(), title);
        } else {
            TopTrack newTopTrack = TopTrack.builder()
                    .band(band)
                    .title(title)
                    .externalUrl(externalUrl)
                    .build();
            topTrackRepository.save(newTopTrack);
            log.info("TopTrack 저장 완료 - bandName: {}, title: {}", band.getBandName(), title);
        }

        return true;
    }
}
