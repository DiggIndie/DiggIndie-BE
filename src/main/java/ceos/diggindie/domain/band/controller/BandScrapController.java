package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.band.dto.BandScrapRequest;
import ceos.diggindie.domain.band.dto.BandScrapResponse;
import ceos.diggindie.domain.band.service.BandScrapService;
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
public class BandScrapController {

    private final BandScrapService bandScrapService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/my/artists")
    public ResponseEntity<Response<Void>> toggleBandScraps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandScrapRequest request
    ) {
        bandScrapService.toggleBandScraps(userDetails.getMemberId(), request);
        return Response.success(SuccessCode.GET_SUCCESS, "밴드 스크랩이 처리되었습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/artists")
    public ResponseEntity<Response<List<BandScrapResponse.BandScrapInfoDTO>>> getBandScraps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BandScrapResponse.BandScrapInfoDTO> scrapPage =
                bandScrapService.getBandScraps(userDetails.getMemberId(), pageable);

        return Response.success(SuccessCode.GET_SUCCESS, scrapPage);
    }
}