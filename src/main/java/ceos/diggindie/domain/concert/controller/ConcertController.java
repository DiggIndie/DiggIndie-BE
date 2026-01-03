package ceos.diggindie.domain.concert.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.concert.dto.ConcertScrapResponse;
import ceos.diggindie.domain.concert.service.ConcertScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertScrapService concertScrapService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/concerts")
    public ResponseEntity<ApiResponse<ConcertScrapResponse.ConcertScrapListDTO>> getMyScrappedConcerts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ConcertScrapResponse.ConcertScrapListDTO response =
                concertScrapService.getMyScrappedConcerts(customUserDetails.getUserId());

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}