package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.dto.BandRecommendResponse;
import ceos.diggindie.domain.band.service.BandRecommendService;
import ceos.diggindie.domain.band.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Band", description = "밴드 관련 API")
@RestController
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;
    private final BandRecommendService bandRecommendService;

    @Operation(summary = "밴드 정보 업데이트 [내부용]", description = "Raw 데이터를 기반으로 밴드 정보를 업데이트합니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/bands/update")
    public void updateBands() {

        bandService.processRawBands();

    }

    @Operation(summary = "아티스트 정보 업데이트 [내부용]", description = "아티스트 정보를 업데이트합니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "업데이트 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/bands/artists/update")
    public ResponseEntity<Void> updateArtists() {
        bandService.processArtists();
        return ResponseEntity.noContent().build();
    }

    /* 밴드 검색 */

    @Operation(summary = "온보딩 시 밴드 검색 및 반환", description = "검색어와 페이징 조건으로 밴드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/artists")
    public ResponseEntity<Response<List<BandListResponse>>> getBandList(
            @Parameter(description = "검색어", example = "리도어")
            @RequestParam(required = false, defaultValue = "") String query,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BandListResponse> bands = bandService.getBandList(query, pageable);
        Response<List<BandListResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                bands,
                "온보딩 밴드 검색 및 목록 반환 API"
        );

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "추천 밴드 조회 API", description = "로그인한 회원의 추천 밴드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/artists/recommendations/users")
    public ResponseEntity<Response<BandRecommendResponse.BandListDTO>> getRecommendedBands(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BandRecommendResponse.BandListDTO result = bandRecommendService.getRecommendedBands(userDetails.getMemberId());
        Response<BandRecommendResponse.BandListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "추천 밴드 반환 API"
        );

        return ResponseEntity.ok().body(response);
    }
}
