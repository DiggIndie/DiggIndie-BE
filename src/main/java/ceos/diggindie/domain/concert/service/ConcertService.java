package ceos.diggindie.domain.concert.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertScrapRepository concertScrapRepository;

    // 공연 위클리 캘린더 반환
    @Transactional(readOnly = true)
    public ConcertWeeklyCalendarResponse getConcertWeeklyCalendar(LocalDate date, Pageable pageable) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Page<Concert> concertPage = concertRepository.findByDate(startOfDay, endOfDay, pageable);

        return ConcertWeeklyCalendarResponse.fromPagedConcerts(concertPage);
    }

    // 공연 목록 반환 (검색, 정렬, 페이지네이션)
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
}
