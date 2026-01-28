package ceos.diggindie.domain.keyword.service;

import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.domain.band.entity.Band;
import ceos.diggindie.domain.band.repository.BandRepository;
import ceos.diggindie.domain.keyword.dto.KeywordRequest;
import ceos.diggindie.domain.keyword.dto.KeywordResponse;
import ceos.diggindie.domain.keyword.entity.BandKeyword;
import ceos.diggindie.domain.keyword.entity.Keyword;
import ceos.diggindie.domain.keyword.entity.MemberKeyword;
import ceos.diggindie.domain.keyword.repository.BandKeywordRepository;
import ceos.diggindie.domain.keyword.repository.KeywordRepository;
import ceos.diggindie.domain.keyword.repository.MemberKeywordRepository;
import ceos.diggindie.domain.member.entity.Member;
import ceos.diggindie.domain.member.repository.MemberRepository;
import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.openai.service.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
// import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final MemberKeywordRepository memberKeywordRepository;
    private final MemberRepository memberRepository;
    private final BandRepository bandRepository;
    private final BandKeywordRepository bandKeywordRepository;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;
    private final KeywordService self;

    public KeywordService(
            KeywordRepository keywordRepository,
            MemberKeywordRepository memberKeywordRepository,
            MemberRepository memberRepository,
            BandRepository bandRepository,
            BandKeywordRepository bandKeywordRepository,
            OpenAIService openAIService,
            ObjectMapper objectMapper,
            @Lazy KeywordService self
    ) {
        this.keywordRepository = keywordRepository;
        this.memberKeywordRepository = memberKeywordRepository;
        this.memberRepository = memberRepository;
        this.bandRepository = bandRepository;
        this.bandKeywordRepository = bandKeywordRepository;
        this.openAIService = openAIService;
        this.objectMapper = objectMapper;
        this.self = self;
    }

    public List<KeywordResponse> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(KeywordResponse::from)
                .toList();
    }

    public List<KeywordResponse> getMyKeywords(Long memberId) {
        List<MemberKeyword> memberKeywords = memberKeywordRepository.findAllByMemberIdWithKeyword(memberId);

        return memberKeywords.stream()
                .map(mk -> KeywordResponse.from(mk.getKeyword()))
                .toList();
    }

    @Transactional
    public void setMyKeywords(Long memberId, KeywordRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> GeneralException.notFound("사용자를 찾을 수 없습니다."));

        memberKeywordRepository.deleteAllByMember(member);

        List<Keyword> keywords = keywordRepository.findAllById(request.keywordIds());

        List<Long> foundIds = keywords.stream().map(Keyword::getId).toList();
        List<Long> invalidIds = request.keywordIds().stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST,
                    "유효하지 않은 키워드 ID: " + invalidIds);
        }

        List<MemberKeyword> memberKeywords = keywords.stream()
                .map(keyword -> MemberKeyword.of(member, keyword))
                .toList();

        memberKeywordRepository.saveAll(memberKeywords);
    }

    public void assignKeywordsToBands() {
        List<Band> bands = bandRepository.findAll();
        List<Keyword> allKeywords = keywordRepository.findAll();

        String keywordList = allKeywords.stream()
                .map(Keyword::getKeyword)
                .collect(Collectors.joining(", "));

        Map<String, Keyword> keywordMap = allKeywords.stream()
                .collect(Collectors.toMap(Keyword::getKeyword, Function.identity()));

        int total = bands.size();
        int completed = 0, failed = 0, skipped = 0;

        log.info("========== 키워드 할당 시작 (총 {}개 밴드) ==========", total);

        for (Band band : bands) {
            try {
                if (bandKeywordRepository.existsByBandId(band.getId())) {
                    skipped++;
                    log.info("[{}/{}] {} - 스킵 (이미 존재)", skipped + completed + failed, total, band.getBandName());
                    continue;
                }

                List<String> selectedKeywords = fetchKeywordsFromAI(band, keywordList);

                if (!selectedKeywords.isEmpty()) {
                    self.saveBandKeywords(band.getId(), selectedKeywords, keywordMap);
                    completed++;
                    log.info("[{}/{}] {} - 성공: {}", skipped + completed + failed, total, band.getBandName(), selectedKeywords);
                } else {
                    failed++;
                    log.warn("[{}/{}] {} - 실패 (키워드 추출 실패)", skipped + completed + failed, total, band.getBandName());
                }
            } catch (Exception e) {
                failed++;
                log.error("[{}/{}] {} - 오류: {}", skipped + completed + failed, total, band.getBandName(), e.getMessage());
            }
        }

        log.info("========== 완료: 성공 {}, 실패 {}, 스킵 {} ==========", completed, failed, skipped);
    }

    private List<String> fetchKeywordsFromAI(Band band, String keywordList) {
        String prompt = """
            다음 아티스트 정보를 보고, 아래 키워드 목록에서 가장 어울리는 키워드 2개를 선택해줘.
            
            [아티스트 정보]
            - 이름: %s
            - 대표곡: %s
            
            [키워드 목록]
            %s
            
            반드시 JSON 배열 형식으로만 응답해. 다른 텍스트 없이.
            예시: ["서정적", "잔잔한"]
            """.formatted(
                band.getBandName(),
                band.getMainMusic() != null ? band.getMainMusic() : "정보 없음",
                keywordList
        );

        String response = openAIService.callOpenAI(new PromptRequest(prompt));

        try {
            return objectMapper.readValue(extractJsonArray(response), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("[{}] JSON 파싱 실패: {}", band.getBandName(), e.getMessage());
            return List.of();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBandKeywords(Long bandId, List<String> keywordNames, Map<String, Keyword> keywordMap) {
        Band band = bandRepository.getReferenceById(bandId);

        for (String name : keywordNames) {
            Keyword keyword = keywordMap.get(name.trim());
            if (keyword != null) {
                bandKeywordRepository.save(BandKeyword.builder()
                        .band(band)
                        .keyword(keyword)
                        .build());
            }
        }
    }

    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}