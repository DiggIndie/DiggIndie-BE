package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.response.PageInfo;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.band.dto.BandScrapRequest;
import ceos.diggindie.domain.band.dto.BandScrapResponse;
import ceos.diggindie.domain.band.service.BandScrapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BandScrapController {

    private final BandScrapService bandScrapService;

    @PostMapping("/artists/scraps")
    public ResponseEntity<ApiResponse<Void>> saveBandScraps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandScrapRequest request
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        bandScrapService.saveBandScraps(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }

    @GetMapping("/artists/scraps")
    public ResponseEntity<ApiResponse<BandScrapResponse.BandScrapPageResponse>> getBandScraps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        Pageable pageable = PageRequest.of(page, size);

        Page<BandScrapResponse.BandScrapInfoDTO> scrapPage =
                bandScrapService.getBandScraps(userDetails.getUserId(), pageable);

        BandScrapResponse.BandScrapPageResponse payload =
                BandScrapResponse.BandScrapPageResponse.builder()
                        .scraps(scrapPage.getContent())
                        .build();

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .body(ApiResponse.<BandScrapResponse.BandScrapPageResponse>builder()
                        .statusCode(SuccessStatus._OK.getHttpStatus().value())
                        .isSuccess(true)
                        .message("스크랩 목록 조회 성공")
                        .pageInfo(new PageInfo(
                                scrapPage.getNumber(),
                                scrapPage.getSize(),
                                scrapPage.hasNext(),
                                scrapPage.getTotalElements(),
                                scrapPage.getTotalPages()
                        ))
                        .payload(payload)
                        .build());
    }
}
