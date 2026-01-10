package ceos.diggindie.domain.concert.service;

import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.code.GeneralErrorCode;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.dto.ConcertsListResponse;
import ceos.diggindie.domain.concert.entity.Concert;
import ceos.diggindie.domain.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

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
}
