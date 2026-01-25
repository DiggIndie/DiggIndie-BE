package ceos.diggindie.domain.board.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.enums.MarketType;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.board.dto.market.*;
import ceos.diggindie.domain.board.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Market", description = "마켓 게시판 관련 API")
@RestController
@RequestMapping("/markets")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @Operation(summary = "마켓 게시글 작성", description = "마켓 게시판에 새 게시글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Response<MarketCreateResponse>> createMarket(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MarketCreateRequest request
    ) {
        MarketCreateResponse result = marketService.createMarket(userDetails.getMemberId(), request);

        return ResponseEntity.status(201).body(Response.success(
                SuccessCode.INSERT_SUCCESS,
                result,
                "마켓 게시글 작성 성공"
        ));
    }

    @Operation(summary = "마켓 게시글 상세 조회", description = "마켓 게시글의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{marketId}")
    public ResponseEntity<Response<MarketDetailResponse>> getMarketDetail(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "마켓 게시글 ID", example = "1")
            @PathVariable Long marketId
    ) {
        MarketDetailResponse result = marketService.getMarketDetail(marketId, userDetails.getMemberId());

        return ResponseEntity.ok(Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "마켓 게시글 상세 조회 성공"
        ));
    }

    @Operation(summary = "마켓 게시글 수정", description = "본인의 마켓 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "본인의 게시글만 수정 가능"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{marketId}")
    public ResponseEntity<Response<Void>> updateMarket(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "마켓 게시글 ID", example = "1")
            @PathVariable Long marketId,
            @Valid @RequestBody MarketUpdateRequest request
    ) {
        marketService.updateMarket(userDetails.getMemberId(), marketId, request);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                "마켓 게시글 수정 성공"
        ));
    }

    @Operation(summary = "마켓 게시글 삭제", description = "본인의 마켓 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "본인의 게시글만 삭제 가능"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{marketId}")
    public ResponseEntity<Response<Void>> deleteMarket(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "마켓 게시글 ID", example = "1")
            @PathVariable Long marketId
    ) {
        marketService.deleteMarket(userDetails.getMemberId(), marketId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.DELETE_SUCCESS,
                "마켓 게시글 삭제 성공"
        ));
    }

    @Operation(summary = "마켓 게시글 목록 조회", description = "타입별 마켓 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<Response<MarketListResponse>> getMarketList(
            @Parameter(description = "타입 (SELL: 판매, BUY: 구매, 미입력: 전체)", example = "SELL")
            @RequestParam(required = false) MarketType type,
            @Parameter(description = "검색어", example = "기타")
            @RequestParam(required = false) String query,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        MarketListResponse result = marketService.getMarketList(type, query, pageable);

        return ResponseEntity.ok(Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "마켓 게시글 목록 조회 성공"
        ));
    }

    @Operation(summary = "마켓 게시글 스크랩 토글", description = "마켓 게시글 스크랩을 토글합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "400", description = "자신의 게시글은 스크랩 불가"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{marketId}/scrap")
    public ResponseEntity<Response<ScrapResponse>> toggleMarketScrap(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "마켓 게시글 ID", example = "1")
            @PathVariable Long marketId
    ) {
        ScrapResponse result = marketService.toggleMarketScrap(userDetails.getMemberId(), marketId);

        return ResponseEntity.ok(Response.success(
                SuccessCode.UPDATE_SUCCESS,
                result,
                "마켓 스크랩 토글 성공"
        ));
    }
}