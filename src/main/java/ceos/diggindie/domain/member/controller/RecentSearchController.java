package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.RecentSearchRequest;
import ceos.diggindie.domain.member.dto.RecentSearchResponse;
import ceos.diggindie.domain.member.service.RecentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recent Search", description = "최근 검색어 관련 API")
@RestController
@RequiredArgsConstructor
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "검색어 추가", description = "최근 검색어를 추가합니다. 동일한 검색어가 있으면 가장 최근 시점으로 업데이트됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/search/recent")
    public ResponseEntity<Response<RecentSearchResponse.RecentSearchInfo>> addRecentSearch(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody RecentSearchRequest request) {

        RecentSearchResponse.RecentSearchInfo result =
                recentSearchService.addRecentSearch(customUserDetails.getMemberId(), request);

        Response<RecentSearchResponse.RecentSearchInfo> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                result,
                "검색어 추가 API"
        );

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "검색어 조회", description = "로그인한 사용자의 최근 검색어를 조회합니다. 최신순으로 정렬됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/search/recent")
    public ResponseEntity<Response<RecentSearchResponse.RecentSearchListDTO>> getRecentSearches(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        RecentSearchResponse.RecentSearchListDTO result =
                recentSearchService.getRecentSearches(customUserDetails.getMemberId());

        Response<RecentSearchResponse.RecentSearchListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "검색어 전체 조회 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "개별 검색어 삭제", description = "특정 검색어를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "검색어를 찾을 수 없음")
    })
    @DeleteMapping("/search/recent/{recentSearchId}")
    public ResponseEntity<Response<Void>> deleteRecentSearch(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "삭제할 검색어 ID")
            @PathVariable Long recentSearchId) {

        recentSearchService.deleteRecentSearch(customUserDetails.getMemberId(), recentSearchId);

        Response<Void> response = Response.success(
                SuccessCode.DELETE_SUCCESS,
                "개별 검색어 삭제 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "전체 검색어 삭제", description = "로그인한 사용자의 모든 검색어를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/search/recent")
    public ResponseEntity<Response<Void>> deleteAllRecentSearches(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        recentSearchService.deleteAllRecentSearches(customUserDetails.getMemberId());

        Response<Void> response = Response.success(
                SuccessCode.DELETE_SUCCESS,
                "전체 검색어 삭제 API"
        );

        return ResponseEntity.ok().body(response);
    }
}
