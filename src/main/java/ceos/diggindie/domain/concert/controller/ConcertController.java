package ceos.diggindie.domain.concert.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.concert.dto.ConcertDetailResponse;
import ceos.diggindie.domain.concert.dto.ConcertScrapResponse;
import ceos.diggindie.domain.concert.dto.ConcertWeeklyCalendarResponse;
import ceos.diggindie.domain.concert.dto.ConcertsListResponse;
import ceos.diggindie.domain.concert.service.ConcertScrapService;
import ceos.diggindie.domain.concert.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Concert", description = "공연 관련 API")
@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;
    private final ConcertScrapService concertScrapService;

    @Operation(summary = "공연 목록 조회", description = "전체 공연 목록을 검색/정렬/페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/concerts")
    public ResponseEntity<Response<ConcertsListResponse>> getConcerts(
            @Parameter(description = "정렬 기준 (recent: 공연임박순, view: 조회순, scrap: 스크랩순)", example = "recent")
            @RequestParam(name="order", required = false, defaultValue = "recent") String order,
            @Parameter(description = "검색어 (공연명, 밴드명)", example = "펜타포트")
            @RequestParam(name="query", required = false) String query,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        ConcertsListResponse result = concertService.getConcertList(query, order, pageable);

        Response<ConcertsListResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "공연 목록 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "공연 상세 조회", description = "개별 공연의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "공연을 찾을 수 없음")
    })
    @GetMapping("/concerts/{concertId}")
    public ResponseEntity<Response<ConcertDetailResponse>> getConcertDetail(
            @Parameter(description = "공연 ID", example = "1")
            @PathVariable Long concertId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long memberId = customUserDetails != null ? customUserDetails.getMemberId() : null;
        ConcertDetailResponse result = concertService.getConcertDetail(concertId, memberId);

        Response<ConcertDetailResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "공연 상세 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "공연 위클리 캘린더 조회", description = "특정 날짜의 공연 목록을 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/concerts/calendar")
    public ResponseEntity<Response<ConcertWeeklyCalendarResponse>> getConcertWeeklyCalendar(
            @Parameter(description = "조회할 날짜 (yyyy-mm-dd)", example = "2025-02-07")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            Pageable pageable) {

        ConcertWeeklyCalendarResponse concertWeeklyCalendarResponse = concertService.getConcertWeeklyCalendar(date, pageable);
        Response<ConcertWeeklyCalendarResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                concertWeeklyCalendarResponse,
                "공연 위클리 캘린더 조회 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 공연 스크랩 목록 조회", description = "로그인한 사용자의 공연 스크랩 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/my/concerts")
    public ResponseEntity<Response<ConcertScrapResponse.ConcertScrapListDTO>> getMyScrappedConcerts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ConcertScrapResponse.ConcertScrapListDTO concertsScrapResponse =
                concertScrapService.getMyScrappedConcerts(customUserDetails.getMemberId());
        Response<ConcertScrapResponse.ConcertScrapListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                concertsScrapResponse,
        "스크랩 공연 반환 API"
        );

        return ResponseEntity.ok().body(response);
    }
}