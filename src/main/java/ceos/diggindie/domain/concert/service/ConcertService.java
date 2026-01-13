package ceos.diggindie.domain.concert.service;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.dto.ConcertRecommendResponse;
import ceos.diggindie.common.code.BusinessErrorCode;
import ceos.diggindie.common.exception.BusinessException;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.domain.concert.dto.ConcertDetailResponse;
import ceos.diggindie.domain.concert.dto.ConcertMonthlyCalendarResponse;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.dto.ConcertsListResponse;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.repository.ConcertRepository;
import ceos.diggindie.domain.concert.repository.ConcertScrapRepository;
import ceos.diggindie.domain.concert.repository.BandConcertRepository;
import ceos.diggindie.domain.concert.repository.ConcertScrapRepository;
import ceos.diggindie.domain.member.repository.MemberBandRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertService {

    private static final int RECOMMEND_LIMIT = 3;

    private final ConcertRepository concertRepository;
    private final MemberBandRepository memberBandRepository;
    private final BandConcertRepository bandConcertRepository;
    private final ConcertScrapRepository concertScrapRepository;

    // 공연 위클리 캘린더 반환
    @Transactional(readOnly = true)
    public ConcertWeeklyCalendarResponse getConcertWeeklyCalendar(LocalDate date, Pageable pageable) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Page<Concert> concertPage = concertRepository.findByDate(startOfDay, endOfDay, pageable);

        return ConcertWeeklyCalendarResponse.fromPagedConcerts(concertPage);
    }

    // 추천 공연 반환
    @Transactional(readOnly = true)
    public ConcertRecommendResponse getRecommendation(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<Long> recommendedConcertIds = new ArrayList<>();

        // 1. 사용자의 선호 밴드 ID 조회
        List<Long> preferredBandIds = memberBandRepository.findBandIdsByMemberId(memberId);

        // 2. 선호 밴드 기반 추천 공연 조회
        if (!preferredBandIds.isEmpty()) {
            List<Object[]> bandBasedConcerts = bandConcertRepository
                    .findConcertIdsByBandIdsOrderByBandCount(preferredBandIds, now);

            for (Object[] result : bandBasedConcerts) {
                if (recommendedConcertIds.size() >= RECOMMEND_LIMIT) break;
                Long concertId = (Long) result[0];
                recommendedConcertIds.add(concertId);
            }
        }

        // 3. 추천할 공연이 3개 미만이면 스크랩이 많은 공연 추가
        if (recommendedConcertIds.size() < RECOMMEND_LIMIT) {
            int remaining = RECOMMEND_LIMIT - recommendedConcertIds.size();
            List<Object[]> scrapBasedConcerts;

            if (recommendedConcertIds.isEmpty()) {
                scrapBasedConcerts = concertScrapRepository.findMostScrappedConcertIds(now);
            } else {
                scrapBasedConcerts = concertScrapRepository
                        .findMostScrappedConcertIds(now, recommendedConcertIds);
            }

            log.info("Scrap based concerts found: {}", scrapBasedConcerts.size());

            for (Object[] result : scrapBasedConcerts) {
                if (recommendedConcertIds.size() >= RECOMMEND_LIMIT) break;
                Long concertId = (Long) result[0];
                recommendedConcertIds.add(concertId);
            }
        }

        // 4. 추천 공연이 없으면 빈 응답 반환
        if (recommendedConcertIds.isEmpty()) {
            return new ConcertRecommendResponse(Collections.emptyList());
        }

        // 5. 공연 상세 정보 조회
        List<Concert> concerts = concertRepository.findAllByIdWithBandConcerts(recommendedConcertIds);

        // 추천 순서 정렬
        Map<Long, Concert> concertMap = concerts.stream()
                .collect(Collectors.toMap(Concert::getId, c -> c));

        List<Concert> orderedConcerts = recommendedConcertIds.stream()
                .map(concertMap::get)
                .filter(c -> c != null)
                .toList();

        return ConcertRecommendResponse.fromConcerts(orderedConcerts);
    }

    // 공연 목록 반환
    @Transactional(readOnly = true)
    public ConcertsListResponse getConcertList(String query, String order, Pageable pageable) {

        LocalDateTime now = LocalDateTime.now();

        Page<Concert> concertPage = switch (order) {
            case "recent" -> concertRepository.findAllByRecent(query, now, pageable);
            case "view" -> concertRepository.findAllByViews(query, now, pageable);
            case "scrap" -> concertRepository.findAllByScrapCount(query, now, pageable);
            default -> throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_PARAMETER,
                    "지원하지 않는 정렬 타입입니다: " + order + ". (recent, view, scrap 중 선택해주세요.)");
        };

        return ConcertsListResponse.fromPagedConcerts(concertPage);
    }

    // 공연 상세 조회
    @Transactional
    public ConcertDetailResponse getConcertDetail(Long concertId, Long memberId) {
        Concert concert = concertRepository.findByIdWithDetails(concertId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CONCERT_NOT_FOUND));

        concert.increaseViews();

        boolean isScrapped = false;
        if (memberId != null) {
            isScrapped = concertScrapRepository.existsByMemberIdAndConcertId(memberId, concertId);
        }

        return ConcertDetailResponse.fromConcert(concert, isScrapped);
    }

    // 월별 공연 캘린더 반환
    @Transactional(readOnly = true)
    public ConcertMonthlyCalendarResponse getMonthlyCalendar(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<LocalDate> concertDates = concertRepository.findDistinctConcertDatesByMonth(startOfMonth, endOfMonth);
        Set<LocalDate> concertDateSet = new HashSet<>(concertDates);

        return ConcertMonthlyCalendarResponse.from(year, month, concertDateSet);
    }

    // 여러 날짜의 공연 목록 반환
    @Transactional(readOnly = true)
    public ConcertsListResponse getConcertsByDates(List<LocalDate> dates, Pageable pageable) {
        Page<Concert> concertPage = concertRepository.findByDates(dates, pageable);
        return ConcertsListResponse.fromPagedConcerts(concertPage);
    }
}
