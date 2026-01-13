package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.domain.spotify.dto.*;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.TopTrack;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.band.repository.TopTrackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ceos.diggindie.domain.spotify.dto.SpotifyTracksResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
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
    private final TransactionTemplate transactionTemplate;

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


    /**
     * 전체 밴드의 TopTrack을 업데이트
     * 각 밴드별 업데이트는 독립적인 트랜잭션으로 처리되어
     * 중간에 실패해도 이미 성공한 업데이트는 커밋됨
     */
    public TopTrackUpdateAllResponse updateAllTopTrack() {
        List<Band> bands = bandRepository.findAll();

        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            log.error("Spotify access token 발급 실패");
            throw new BusinessException(BusinessErrorCode.SPOTIFY_TOKEN_ERROR);
        }
        String accessToken = tokenResponse.accessToken();

        int successCount = 0;
        int failCount = 0;
        int skippedCount = 0;

        for (Band band : bands) {
            // 이미 TopTrack이 있으면 스킵
            if (topTrackRepository.existsByBand(band)) {
                log.debug("이미 TopTrack이 존재하는 밴드 스킵 - bandId: {}, bandName: {}", band.getId(), band.getBandName());
                skippedCount++;
                continue;
            }

            String spotifyId = band.getSpotifyId();

            // spotifyId가 없는 밴드는 스킵
            if (spotifyId == null || spotifyId.isBlank()) {
                log.info("Spotify ID가 없는 밴드 스킵 - bandId: {}, bandName: {}", band.getId(), band.getBandName());
                failCount++;
                continue;
            }

            try {
                SpotifyTopTrackResponse topTracksResponse = getTopTracks(spotifyId, accessToken);

                if (topTracksResponse == null || topTracksResponse.tracks().isEmpty()) {
                    log.info("Top Track이 없는 밴드 스킵 - bandId: {}, bandName: {}", band.getId(), band.getBandName());
                    failCount++;
                    continue;
                }

                // TransactionTemplate으로 개별 트랜잭션 처리
                Boolean saved = transactionTemplate.execute(status -> {
                    try {
                        SpotifyTopTrackResponse.TrackDto topTrack = topTracksResponse.tracks().getFirst();
                        String title = topTrack.name();
                        String externalUrl = topTrack.external_urls().spotify();

                        TopTrack newTopTrack = TopTrack.builder()
                                .band(band)
                                .title(title)
                                .externalUrl(externalUrl)
                                .build();
                        topTrackRepository.save(newTopTrack);
                        log.info("TopTrack 저장 완료 - bandName: {}, title: {}", band.getBandName(), title);
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.error("TopTrack 저장 실패 - bandId: {}, error: {}", band.getId(), e.getMessage());
                        return false;
                    }
                });

                if (Boolean.TRUE.equals(saved)) {
                    successCount++;
                } else {
                    failCount++;
                }

            } catch (Exception e) {
                log.error("TopTrack 처리 실패 - bandId: {}, bandName: {}, error: {}",
                        band.getId(), band.getBandName(), e.getMessage());
                failCount++;
            }
        }

        log.info("TopTrack 동기화 완료 - 총 {}개 밴드 중 {}개 성공, {}개 스킵, {}개 실패",
                bands.size(), successCount, skippedCount, failCount);
        return new TopTrackUpdateAllResponse(bands.size(), successCount, failCount);
    }

    @Transactional
    public TopTrackUpdateResponse updateTopTrackByBandId(Long bandId) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BAND_NOT_FOUND));

        String spotifyId = band.getSpotifyId();
        if (spotifyId == null || spotifyId.isBlank()) {
            log.error("Spotify ID가 없는 밴드입니다 - bandId: {}", bandId);
            throw new BusinessException(BusinessErrorCode.SPOTIFY_ID_NOT_FOUND);
        }

        SpotifyTokenResponse tokenResponse = getSpotifyToken();
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            log.error("Spotify access token 발급 실패");
            throw new BusinessException(BusinessErrorCode.SPOTIFY_TOKEN_ERROR);
        }

        SpotifyTopTrackResponse topTracksResponse = getTopTracks(spotifyId, tokenResponse.accessToken());

        if (topTracksResponse == null || topTracksResponse.tracks().isEmpty()) {
            log.info("Top Track이 없는 밴드 - bandId: {}", bandId);
            throw new BusinessException(BusinessErrorCode.TOP_TRACK_NOT_FOUND);
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

        return new TopTrackUpdateResponse(bandId, true);
    }
}