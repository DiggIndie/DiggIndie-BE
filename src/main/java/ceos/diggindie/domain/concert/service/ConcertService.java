package ceos.diggindie.domain.concert.service;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.dto.ConcertRecommendResponse;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.repository.ConcertRepository;
import ceos.diggindie.domain.concert.repository.BandConcertRepository;
import ceos.diggindie.domain.member.repository.MemberBandRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final MemberBandRepository memberBandRepository;
    private final BandConcertRepository bandConcertRepository;

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
        // 1. 선호 밴드와 공연을 조인하여 matchCount 계산, 공연일 오름차순, matchCount 내림차순으로 3개 공연 ID 조회
        List<Long> topConcertIds = bandConcertRepository.findTopConcertIdsByMemberPreference(
            memberId, LocalDateTime.now(), 3
        );

        // 2. 공연 ID로 공연 엔티티 조회
        List<Concert> concerts = concertRepository.findAllWithBandsByIdIn(topConcertIds);

        // 3. 부족할 경우 fallback 쿼리로 스크랩 많은 공연 추가 -> 수정필요
        if (concerts.size() < 3) {
//            int remain = 3 - concerts.size();
//            List<Long> excludeIds = new ArrayList<>(topConcertIds);
//            List<Concert> fallbackConcerts = concertRepository.findTopScrappedConcertsAfterNowExcludeIds(
//                LocalDateTime.now(), excludeIds, PageRequest.of(0, remain)
//            );
//            concerts.addAll(fallbackConcerts);
        }

        return ConcertRecommendResponse.fromConcerts(concerts);
    }
}
