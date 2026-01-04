package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/bands/artists/update")
    public void updateArtists() {
        bandService.processArtists();
    }

    /* 밴드 검색 */

    @GetMapping("/artists")
    public ResponseEntity<Response<List<BandListResponse>>> getBandList(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BandListResponse> bands = bandService.getBandList(query, pageable);

        return Response.success(SuccessCode.GET_SUCCESS, bands);
    }
}
