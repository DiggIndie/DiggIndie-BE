package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.BandPreferenceRequest;
import ceos.diggindie.domain.member.dto.BandPreferenceResponse;
import ceos.diggindie.domain.member.service.MemberBandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member Band", description = "멤버 밴드 취향 관련 API")
@RestController
@RequiredArgsConstructor
public class MemberBandController {

    private final MemberBandService memberBandService;

    @Operation(summary = "밴드 취향 저장", description = "로그인 사용자의 밴드 취향 정보를 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "저장 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/artists/preferences")
    public ResponseEntity<Response<Void>> saveBandPreferences(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandPreferenceRequest request
    ) {

        memberBandService.saveBandPreferences(userDetails.getMemberId(), request);
        Response<Void> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                "밴드 취향 설정 API"
        );

        return ResponseEntity.status(204).body(response);
    }

    @Operation(summary = "밴드 취향 조회", description = "로그인 사용자의 밴드 취향 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/artists/preferences")
    public ResponseEntity<Response<BandPreferenceResponse>> getBandPreferences(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        BandPreferenceResponse bandPreferenceResponse = memberBandService.getBandPreferences(userDetails.getMemberId());
        Response<BandPreferenceResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                bandPreferenceResponse,
                "밴드 취향 조회 API"
        );

        return ResponseEntity.ok().body(response);
    }
}
