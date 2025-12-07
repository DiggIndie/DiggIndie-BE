package ceos.diggindie.domain.band.service;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.BandsRawData;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.band.repository.BandsRawDataRepository;
import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.openai.service.OpenAIService;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;
    private final BandsRawDataRepository bandsRawDataRepository;

    private final OpenAIService openAIService;
    private final SpotifyService spotifyService;

    public void processRawBands() {

        // 1. BandsRawData에서 개별 밴드 데이터 조회 (bandName, MainMusic, description)
        List<BandsRawData> raws = bandsRawDataRepository.findAll();

        int total = raws.size();
        int completed = 0;
        int failed = 0;

        log.info("========== 밴드 생성 시작 (총 {}개) ==========", total);

        for (BandsRawData raw : raws) {

            try {
                String bandName = raw.getBandName();
                String rawMainMusic = raw.getMainMusic();
                String rawDescription = raw.getDescription();

                // 2. OpenAIService를 통해 main_music, description 데이터 가공
                String refinedMainMusic = null;
                try {
                    String promptForMusic = "다음 텍스트에서 이 밴드의 대표곡 제목을 뒤쪽의 [1] 형태의 숫자가 있다면 제거하고 간결하게 정리해줘. 정리할 내용이 없다면 그냥 기존 제목을 그대로 반환해. 대표곡 : " + (rawMainMusic == null ? "" : rawMainMusic);
                    refinedMainMusic = openAIService.callOpenAI(new PromptRequest(promptForMusic));
                    if (refinedMainMusic == null || refinedMainMusic.isBlank()) {
                        refinedMainMusic = rawMainMusic;
                    }
                } catch (Exception e) {
                    log.warn("[{}] GPT 대표곡 처리 오류: {}", bandName, e.getMessage());
                    refinedMainMusic = null;
                }

                String refinedDescription = null;
                try {
                    String promptForDescription = "다음 밴드 설명에서 [1] 처럼 불필요한 숫자 표기가 있다면 제거하고 반환해줘. 말투도 -이다. 형태로 변환해줘 원문: " + (rawDescription == null ? "" : rawDescription);
                    refinedDescription = openAIService.callOpenAI(new PromptRequest(promptForDescription));
                    if (refinedDescription == null || refinedDescription.isBlank()) {
                        refinedDescription = rawDescription;
                    }
                } catch (Exception e) {
                    log.warn("[{}] GPT 설명 처리 오류: {}", bandName, e.getMessage());
                    refinedDescription = null;
                }

                // 3. SpotifyService를 통해 bandName으로 아티스트 조회 및 추가 정보 획득 (spotifyId, imageUrl)
                String spotifyId = null;
                String imageUrl = null;
                String mainUrl = null;

                try {
                    SpotifySearchResponse response = spotifyService.searchSpotify(new SpotifySearchRequest(bandName));
                    if (response != null && response.artists() != null && response.artists().items() != null && !response.artists().items().isEmpty()) {
                        SpotifySearchResponse.Artist artist = response.artists().items().get(0);
                        spotifyId = artist.id();
                        imageUrl = (artist.images() == null || artist.images().isEmpty()) ? null : artist.images().get(0).url();
                        mainUrl = (artist.external_urls() == null) ? null : artist.external_urls().spotify();
                    }
                } catch (Exception e) {
                    log.warn("[{}] Spotify 검색 오류: {}", bandName, e.getMessage());
                }

                // 4. Band 엔티티 생성 및 저장
                Band band = Band.builder()
                        .bandName(bandName)
                        .mainImage(imageUrl)
                        .mainUrl(mainUrl)
                        .mainMusic(refinedMainMusic)
                        .description(refinedDescription)
                        .spotifyId(spotifyId)
                        .build();

                bandRepository.save(band);
                completed++;

                log.info("[{}/{}] {} 저장 완료", completed, total, bandName);

            } catch (Exception e) {
                failed++;
                log.error("[실패] 밴드 생성 중 오류 발생: {}", e.getMessage());
            }
        }

        log.info("========== 밴드 생성 완료 ==========");
        log.info("총 {}개 중 성공: {}개, 실패: {}개", total, completed, failed);
    }
}
