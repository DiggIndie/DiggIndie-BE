package ceos.diggindie.domain.magazine.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.enums.MagazineSortOrder;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.magazine.dto.MagazineResponse;
import ceos.diggindie.domain.magazine.service.MagazineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Magazine", description = "매거진 관련 API")
@RestController
@RequiredArgsConstructor
public class MagazineController {

    private final MagazineService magazineService;

    @Operation(summary = "매거진 검색 및 목록 조회", description = "검색어, 정렬 조건, 페이징으로 매거진 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/magazines")
    public ResponseEntity<Response<MagazineResponse.MagazineListDTO>> searchMagazines(
            @Parameter(description = "정렬 기준 (recent: 최신순, view: 조회수순)", example = "recent")
            @RequestParam(required = false, defaultValue = "recent") MagazineSortOrder order,
            @Parameter(description = "검색어 (제목에서 검색)", example = "인디")
            @RequestParam(required = false, defaultValue = "") String query,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MagazineResponse.MagazineListDTO result = magazineService.searchMagazines(query, order, pageable);
        Response<MagazineResponse.MagazineListDTO> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "매거진 목록 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "매거진 상세 조회", description = "매거진 ID로 상세 정보를 조회합니다. 조회 시 조회수가 1 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "매거진을 찾을 수 없음")
    })
    @GetMapping("/magazines/{magazineId}")
    public ResponseEntity<Response<MagazineResponse.MagazineInfo>> getMagazineDetail(
            @Parameter(description = "매거진 ID", example = "1")
            @PathVariable Long magazineId
    ) {
        MagazineResponse.MagazineInfo result = magazineService.getMagazineDetail(magazineId);
        Response<MagazineResponse.MagazineInfo> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "매거진 상세 조회 성공"
        );
        return ResponseEntity.ok().body(response);
    }
}


