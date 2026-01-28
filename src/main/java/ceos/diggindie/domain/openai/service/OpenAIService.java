package ceos.diggindie.domain.openai.service;

import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.entity.BandDescription;
import ceos.diggindie.domain.band.repository.BandDescriptionRepository;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.openai.dto.OpenAIRequest;
import ceos.diggindie.domain.openai.dto.OpenAIResponse;
import ceos.diggindie.domain.openai.dto.PromptRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.client-id}")
    private String CLIENT_ID;

    @Value("${openai.model}")
    private String MODEL;

    private final RestClient restClient;
    private final BandRepository bandRepository;
    private final BandDescriptionRepository bandDescriptionRepository;

    public String callOpenAI(PromptRequest request) {

        OpenAIRequest openAIRequest = new OpenAIRequest(
                MODEL,
                request.prompt()
        );

        OpenAIResponse response = restClient
                .post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + CLIENT_ID)
                .body(openAIRequest)
                .retrieve()
                .body(OpenAIResponse.class);

        if (response == null || response.output() == null || response.output().isEmpty()) {
            return null;
        }

        String result = response.output().stream()
                .filter(o -> o.content() != null)
                .flatMap(o -> o.content().stream())
                .filter(c -> c.text() != null)
                .map(OpenAIResponse.ContentItem::text)
                .collect(Collectors.joining("\n"));

        return result;
    }

    @Transactional
    public String generateBandDescriptions(Long startBandId) {
        // 1. 특정 band_id 이후의 모든 밴드 조회
        List<Band> bands = bandRepository.findAll().stream()
                .filter(band -> band.getId() >= startBandId)
                .toList();

        int total = bands.size();
        int completed = 0;
        int skipped = 0;
        int failed = 0;

        log.info("========== 밴드 설명 생성 시작 (총 {}개, ID >= {}) ==========", total, startBandId);

        for (Band band : bands) {
            try {
                // 2. 이미 설명이 있는 경우 스킵
                if (bandDescriptionRepository.findByBandId(band.getId()).isPresent()) {
                    skipped++;
                    log.info("[{}/{}] {} - 스킵 (이미 존재)", completed + skipped + failed, total, band.getBandName());
                    continue;
                }

                // 3. GPT 프롬프트 생성
                String prompt = String.format("""
                        다음 가수에 대한 음악적 특색을 드러내는 간결한 설명을 작성해줘.
                        
                        가수/밴드명: %s
                        
                        작성 규칙:
                        - 최대 6문장으로 작성
                        - 음악 장르, 멜로디 특징, 가사 주제, 공연 특징 등을 포함
                        - "-이다" 형태의 정중한 문체 사용
                        - 설명만 작성하고 다른 멘트는 절대 포함하지 말 것
                        
                        예시:
                        극동아시아타이거즈는 펑키하면서도 신나는 멜로디와 공감 가는 가사를 특징으로 하는 펑크 록 밴드이다. 우당탕탕 음악으로 추억을 노래하는 밴드라는 슬로건처럼 정제되지 않은 날 것의 느낌과 익살스러움 속에 향수를 담아내며, 라이브 공연에서 특히 빛을 발한다.
                        
                        정고래는 감성적인 발라드와 인디팝을 중심으로 따뜻하고 담백한 멜로디와 편곡이 특징인 솔로 아티스트이다. 주로 잊혀지지 않는 기억과 이별, 위로를 주제로 한 곡들을 선보인다
                        """, band.getBandName());

                // 4. GPT 호출
                String description = callOpenAI(new PromptRequest(prompt));

                if (description == null || description.isBlank()) {
                    failed++;
                    log.warn("[{}/{}] {} - 실패 (GPT 응답 없음)", completed + skipped + failed, total, band.getBandName());
                    continue;
                }

                // 5. BandDescription 저장
                BandDescription bandDescription = BandDescription.builder()
                        .band(band)
                        .description(description.trim())
                        .build();

                bandDescriptionRepository.save(bandDescription);
                completed++;

                log.info("[{}/{}] {} - 완료", completed + skipped + failed, total, band.getBandName());

            } catch (Exception e) {
                failed++;
                log.error("[{}/{}] {} - 오류: {}", completed + skipped + failed, total, band.getBandName(), e.getMessage());
            }
        }

        String result = String.format(
                "밴드 설명 생성 완료\n총 %d개 중 성공: %d개, 스킵: %d개, 실패: %d개",
                total, completed, skipped, failed
        );

        log.info("========== 완료 ==========");
        log.info(result);

        return result;
    }
}
