package ceos.diggindie.domain.band.controller;

import ceos.diggindie.domain.band.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
