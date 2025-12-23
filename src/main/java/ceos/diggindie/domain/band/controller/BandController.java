package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.band.dto.BandListResponse;
import ceos.diggindie.domain.band.dto.BandScrapRequest;
import ceos.diggindie.domain.band.dto.BandScrapResponse;
import ceos.diggindie.domain.band.service.BandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /* 밴드 검색, 밴드 취향 설정 (GET, POST) */

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

    @PostMapping("/artists/preferences")
    public ResponseEntity<ApiResponse<Void>> saveBandPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandScrapRequest request
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        bandService.saveBandPreferences(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }

    @GetMapping("/artists/preferences")
    public ResponseEntity<ApiResponse<BandScrapResponse>> getBandPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        BandScrapResponse response = bandService.getBandPreferences(userDetails.getUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
