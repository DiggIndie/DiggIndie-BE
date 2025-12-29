package ceos.diggindie.domain.concert.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.concert.dto.ConcertRecommendResponse;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Concert", description = "공연 관련 API")
@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @Operation(summary = "공연 위클리 캘린더 조회", description = "특정 날짜의 공연 목록을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/concerts")
    public ResponseEntity<Response<ConcertWeeklyCalendarResponse>> getConcertWeeklyCalendar(
            @Parameter(description = "조회할 날짜 (yyyy-mm-dd)", example = "2025-02-07")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "페이지 번호 (0부터 시작)")
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

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "추천 공연 조회", description = "추천 공연 목록을 조회합니다.")
    @GetMapping("/concerts/recommendations")
    public ResponseEntity<Response<ConcertRecommendResponse>> getConcertRecmmedations(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        Response<ConcertRecommendResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "추천 공연 조회 API",
                concertService.getRecommendation(userDetails.getMemberId())
        );
        return ResponseEntity.ok().body(response);
    }

}
