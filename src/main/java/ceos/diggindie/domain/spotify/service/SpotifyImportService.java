package ceos.diggindie.domain.spotify.service;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.repository.BandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyImportService {

    private final SpotifyService spotifyService;
    private final BandRepository bandRepository;
    private final AlbumSaveService albumSaveService;

    /**
     * 모든 밴드의 앨범/곡 데이터 import
     */
    @Transactional
    public void importAllBandsAlbums() {
        List<Band> bands = bandRepository.findAll();

        int total = bands.size();
        int completed = 0;
        int failed = 0;

        log.info("========== 앨범/곡 Import 시작 (총 {}개 밴드) ==========", total);

        for (Band band : bands) {
            try {
                importBandAlbums(band);
                completed++;
                log.info("[{}/{}] {} 완료", completed, total, band.getBandName());

                // Rate limit 방지
                Thread.sleep(100);
            } catch (Exception e) {
                failed++;
                log.error("[실패] {} - {}", band.getBandName(), e.getMessage());
            }
        }

        log.info("========== Import 완료: 성공 {}, 실패 {} ==========", completed, failed);
    }

    /**
     * 단일 밴드의 앨범/곡 import
     */
    // @Transactional save service에서 처리함
    public void importBandAlbums(Band band) {
        if (band.getSpotifyId() == null) {
            log.warn("Spotify ID 없음: {}", band.getBandName());
            return;
        }

        try {
            var albumsResponse = spotifyService.getArtistAlbums(band.getSpotifyId(), 10);

            for (var spotifyAlbum : albumsResponse.items()) {
                try {
                    var tracksResponse = spotifyService.getAlbumTracks(spotifyAlbum.id());
                    albumSaveService.saveAlbumWithTracks(spotifyAlbum, tracksResponse.items(), band);
                } catch (Exception e) {
                    log.error("앨범 저장 실패: {} - {}", spotifyAlbum.name(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("밴드 앨범 조회 실패: {} - {}", band.getBandName(), e.getMessage());
        }
    }
}