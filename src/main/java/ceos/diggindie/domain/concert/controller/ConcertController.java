package ceos.diggindie.domain.concert.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping("/concerts")
    public ResponseEntity<Response<ConcertWeeklyCalendarResponse>> getConcertWeeklyCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable
            ) {
        Response<ConcertWeeklyCalendarResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "공연 위클리 캘린더 조회 API",
                concertService.getConcertWeeklyCalendar(date, pageable)
        );
        return ResponseEntity.ok().body(response);
    }

}
