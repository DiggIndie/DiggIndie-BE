package ceos.diggindie.domain.concert.service;

import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
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
}
