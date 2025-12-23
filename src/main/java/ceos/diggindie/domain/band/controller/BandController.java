package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.service.BandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/bands/update")
    public void updateBands() {

        bandService.processRawBands();

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/bands/artists/update")
    public void updateArtists() {
        bandService.processArtists();
    }

    /* 밴드 검색 */

    @GetMapping("/artists/lists")
    public ResponseEntity<ApiResponse<List<BandListResponse>>> getBandList(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BandListResponse> bands = bandService.getBandList(query, pageable);

        return ApiResponse.onSuccess(SuccessStatus._OK, bands);
    }
}
