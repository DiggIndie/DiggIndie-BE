package ceos.diggindie.domain.band.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.band.dto.BandScrapRequest;
import ceos.diggindie.domain.band.dto.BandScrapResponse;
import ceos.diggindie.domain.band.service.BandScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static ceos.diggindie.common.response.Response.success;

@Tag(name = "Band Scrap", description = "밴드 스크랩 관련 API")
@RestController
@RequiredArgsConstructor
public class BandScrapController {

    private final BandScrapService bandScrapService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "밴드 스크랩 토글", description = "로그인 사용자의 밴드 스크랩을 추가/해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "스크랩 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/my/artists")
    public ResponseEntity<Response<Void>> toggleBandScraps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandScrapRequest request
    ) {

        bandScrapService.toggleBandScraps(userDetails.getMemberId(), request);
        Response<Void> response = Response.success(
                SuccessCode.GET_SUCCESS,
                "밴드 스크랩이 처리되었습니다."
        );

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "밴드 스크랩 목록 조회", description = "로그인 사용자의 밴드 스크랩 목록을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/my/artists")
    public ResponseEntity<Response<List<BandScrapResponse.BandScrapInfoDTO>>> getBandScraps(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BandScrapResponse.BandScrapInfoDTO> scrapPageReponse =
                bandScrapService.getBandScraps(userDetails.getMemberId(), pageable);
        Response<List<BandScrapResponse.BandScrapInfoDTO>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                scrapPageReponse,
                "밴드 스크랩 목록 조회 API"
        );

        return ResponseEntity.ok().body(response);
    }
}