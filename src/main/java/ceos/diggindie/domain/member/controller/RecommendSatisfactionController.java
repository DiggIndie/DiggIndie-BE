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
                           "- PERFECT_MATCH: 취향에 딱 맞아요\n" +
                           "- NEW_DISCOVERY: 새로운 발견이 가능했어요\n\n" +
                           "**불만족한 경우(satisfied=false):**\n" +
                           "- ALREADY_KNOWN: 이미 알고 있는 아티스트에요\n" +
                           "- NOT_MY_TASTE: 취향과 상관없는 음악 같아요\n" +
                           "- KEYWORD_MISMATCH: 키워드와 실제 음악이 매칭되지 않아요\n" +
                           "- GENRE_FINE_TRACK_NOT_MY_TASTE: 장르는 맞는데 노래가 취향이 아니에요\n" +
                           "- BORED: 비슷한 스타일만 나와 지루해요\n" +
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

