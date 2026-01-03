package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.member.dto.BandPreferenceRequest;
import ceos.diggindie.domain.member.dto.BandPreferenceResponse;
import ceos.diggindie.domain.member.service.MemberBandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberBandController {

    private final MemberBandService memberBandService;

    @PostMapping("/artists/preferences")
    public ResponseEntity<ApiResponse<Void>> saveBandPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody BandPreferenceRequest request
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        memberBandService.saveBandPreferences(userDetails.getUserId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED, "밴드 취향 설정 API");
    }

    @GetMapping("/artists/preferences")
    public ResponseEntity<ApiResponse<BandPreferenceResponse>> getBandPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        BandPreferenceResponse response = memberBandService.getBandPreferences(userDetails.getUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
