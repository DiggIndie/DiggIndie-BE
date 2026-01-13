package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.enums.SearchCategory;
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
    @Operation(summary = "검색어 추가", description = "최근 검색어를 추가합니다.")
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
                "검색어 추가 성공"
        );

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "카테고리별 검색어 조회", description = "로그인한 사용자의 카테고리별 최근 검색어를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/search/recent")
    public ResponseEntity<Response<RecentSearchResponse.RecentSearchListDTO>> getRecentSearches(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "검색 카테고리 (COMMUNITY, BAND, CONCERT, GENERAL)")
            @RequestParam SearchCategory category) {

        RecentSearchResponse.RecentSearchListDTO result =
                recentSearchService.getRecentSearchesByCategory(customUserDetails.getMemberId(), category);

        Response<RecentSearchResponse.RecentSearchListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "검색어 조회 성공"
        );

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "개별 검색어 삭제", description = "특정 검색어를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
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
                "검색어 삭제 성공"
        );

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "전체 검색어 삭제", description = "로그인한 사용자의 전체 검색어를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/search/recent")
    public ResponseEntity<Response<Void>> deleteAllRecentSearches(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "삭제할 카테고리 (COMMUNITY, BAND, CONCERT, GENERAL). 미지정 시 전체 삭제")
            @RequestParam(required = false) SearchCategory category) {

        if (category != null) {
            recentSearchService.deleteRecentSearchesByCategory(customUserDetails.getMemberId(), category);
        } else {
            recentSearchService.deleteAllRecentSearches(customUserDetails.getMemberId());
        }

        Response<Void> response = Response.success(
                SuccessCode.DELETE_SUCCESS,
                category != null ? category.getDescription() + " 검색어 전체 삭제 성공" : "전체 검색어 삭제 성공"
        );

        return ResponseEntity.ok().body(response);
    }
}
