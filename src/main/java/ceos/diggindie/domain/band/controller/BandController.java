package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.band.dto.BandDetailResponse;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.dto.BandSearchResponse;
import ceos.diggindie.common.enums.BandSortOrder;
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

    /* 밴드 검색 - 온보딩용 */
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

    /* 아티스트 검색 및 목록 조회 */
    @Operation(summary = "아티스트 검색 및 목록 조회", description = "검색어, 정렬 조건, 페이징으로 아티스트 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/artists/search")
    public ResponseEntity<Response<BandSearchResponse.ArtistListDTO>> searchArtists(
            @Parameter(description = "정렬 기준 (recent: 최신순, alphabet: 가나다순, scrap: 스크랩순)", example = "recent")
            @RequestParam(required = false, defaultValue = "recent") BandSortOrder order,
            @Parameter(description = "검색어 (아티스트명, 키워드)", example = "쏜애플")
            @RequestParam(required = false, defaultValue = "") String query,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        BandSearchResponse.ArtistListDTO result = bandService.searchArtists(query, order, pageable);
        Response<BandSearchResponse.ArtistListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "아티스트 목록 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }

    /* 아티스트 상세 조회 */
    @PreAuthorize("permitAll()")
    @Operation(summary = "아티스트 상세 조회", description = "아티스트의 상세 정보를 조회합니다. 로그인 시 스크랩 여부가 포함됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 아티스트")
    })
    @GetMapping("/artists/{bandId}")
    public ResponseEntity<Response<BandDetailResponse>> getBandDetail(
            @Parameter(description = "아티스트 ID", example = "12")
            @PathVariable Long bandId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        BandDetailResponse bandDetail = bandService.getBandDetail(bandId, memberId);

        Response<BandDetailResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                bandDetail,
                "아티스트 상세 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }
}