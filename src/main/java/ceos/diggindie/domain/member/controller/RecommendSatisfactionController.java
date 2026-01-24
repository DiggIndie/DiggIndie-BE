package ceos.diggindie.domain.member.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.response.Response;
import ceos.diggindie.domain.member.dto.RecommendSatisfactionRequest;
import ceos.diggindie.domain.member.dto.RecommendSatisfactionResponse;
import ceos.diggindie.domain.member.service.RecommendSatisfactionService;
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

@Tag(name = "Recommend Satisfaction", description = "추천 만족도 관련 API")
@RestController
@RequiredArgsConstructor
public class RecommendSatisfactionController {

    private final RecommendSatisfactionService recommendSatisfactionService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "추천 만족도 추가",
               description = "회원의 추천 만족도를 기록합니다. " +
                           "satisfied는 필수이며, reason은 선택 사항입니다.\n\n" +
                           "**만족한 경우(satisfied=true):**\n" +
                           "- PERFECT_MATCH: 취향 저격\n" +
                           "- NEW_DISCOVERY: 새로운 발견\n" +
                           "- DIVERSE_SELECTION: 다양한 선택지\n\n" +
                           "**불만족한 경우(satisfied=false):**\n" +
                           "- NOT_MY_TASTE: 취향이 아님\n" +
                           "- ALREADY_KNOWN: 이미 아는 아티스트\n" +
                           "- GENRE_MISMATCH: 장르가 안 맞음\n" +
                           "- TOO_MAINSTREAM: 너무 대중적임\n" +
                           "- OTHER: 기타")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "추가 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/artists/recommendations/satisfaction")
    public ResponseEntity<Response<RecommendSatisfactionResponse.RecommendSatisfactionInfo>> addRecommendSatisfaction(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody RecommendSatisfactionRequest request) {

        RecommendSatisfactionResponse.RecommendSatisfactionInfo result =
                recommendSatisfactionService.addRecommendSatisfaction(customUserDetails.getMemberId(), request);

        Response<RecommendSatisfactionResponse.RecommendSatisfactionInfo> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                result,
                "추천 만족도 추가 API"
        );

        return ResponseEntity.status(201).body(response);
    }
}

