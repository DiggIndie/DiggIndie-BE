package ceos.diggindie.domain.band.service;

import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.entity.Artist;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.BandsRawData;
import ceos.diggindie.domain.band.repository.ArtistRepository;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.band.repository.BandsRawDataRepository;
import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.openai.service.OpenAIService;
import ceos.diggindie.domain.spotify.dto.SpotifySearchRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import ceos.diggindie.domain.spotify.service.SpotifyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;
    private final BandsRawDataRepository bandsRawDataRepository;
    private final ArtistRepository artistRepository;

    private final OpenAIService openAIService;
    private final SpotifyService spotifyService;
    private final ObjectMapper objectMapper;

    public void processArtists() {

        List<BandsRawData> raws = bandsRawDataRepository.findAll();

        int total = raws.size();
        int completed = 0;
        int failed = 0;
        int skipped = 0;

        log.info("========== 아티스트 생성 시작 (총 {}개 밴드) ==========", total);

        for (BandsRawData raw : raws) {

            // 1. raw 데이터에서 bandName, artist 정보 추출
            String bandName = raw.getBandName();

            try {

                if (raw.getArtist() == null || raw.getArtist().isBlank() || raw.getArtist().equals("[null]")) {
                    skipped++;
                    log.info("[{}/{}] {} - 아티스트 정보 없음 (스킵)", completed + failed + skipped, total, bandName);
                    continue;
                }

                // 2. 밴드 조회
                Band band = bandRepository.findByBandName(bandName).orElse(null);
                if (band == null) {
                    failed++;
                    log.warn("[{}/{}] {} - 밴드를 찾을 수 없음", completed + failed + skipped, total, bandName);
                    continue;
                }

                // 3. GPT를 활용해 아티스트 이름 가공 및 파싱
                String prompt = """
                    다음 텍스트에서 아티스트 이름만 추출해줘.
                    - [1], [2] 같은 숫자 표기는 제거
                    - 괄호 안의 포지션 정보(기타, 보컬, 드럼 등)는 제거하고 이름만 추출
                    - 반드시 JSON 배열만 반환 (다른 텍스트 없이)
                    - 형식: ["이름1", "이름2", "이름3"]
                    
                    예시 입력: "권선제(베이스, 보컬), 양지연(드럼, 코러스)"
                    예시 출력: ["권선제", "양지연"]
                    
                    텍스트: %s
                    """.formatted(raw.getArtist());

                String response = openAIService.callOpenAI(new PromptRequest(prompt));

                if (response == null || response.isBlank()) {
                    failed++;
                    log.warn("[{}/{}] {} - GPT 응답 없음", completed + failed + skipped, total, bandName);
                    continue;
                }

                List<String> artistNames = null;
                try {
                    // 4. 응답에서 JSON 배열 부분만 추출
                    String jsonPart = extractJsonArray(response);
                    artistNames = objectMapper.readValue(jsonPart, new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    failed++;
                    log.warn("[{}/{}] {} - JSON 파싱 오류: {}", completed + failed + skipped, total, bandName, e.getMessage());
                    continue;
                }

                // 5. Artist 엔티티 생성 및 저장
                int savedCount = 0;
                for (String name : artistNames) {
                    if (name == null || name.isBlank()) {
                        continue;
                    }

                    Artist artist = Artist.builder()
                            .artistName(name.trim())
                            .band(band)
                            .build();

                    artistRepository.save(artist);
                    savedCount++;
                }

                completed++;
                log.info("[{}/{}] {} - {}명의 아티스트 저장 완료", completed + failed + skipped, total, bandName, savedCount);

            } catch (Exception e) {
                failed++;
                log.error("[{}/{}] {} - 처리 중 오류: {}", completed + failed + skipped, total, bandName, e.getMessage());
            }
        }

        log.info("========== 아티스트 생성 완료 ==========");
        log.info("총 {}개 밴드 중 성공: {}개, 실패: {}개, 스킵: {}개", total, completed, failed, skipped);
    }

    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }


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

    public Page<BandListResponse> getBandList(String query, Pageable pageable) {
        Page<Band> bands = bandRepository.searchBands(query, pageable);
        return bands.map(BandListResponse::from);
    }
}
