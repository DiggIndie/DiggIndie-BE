package ceos.diggindie.domain.keyword.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.keyword.dto.KeywordRequest;
import ceos.diggindie.domain.keyword.dto.KeywordResponse;
import ceos.diggindie.domain.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Keyword", description = "키워드 관련 API")
@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "전체 키워드 조회", description = "서비스에서 제공하는 모든 키워드를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();
        return ApiResponse.onSuccess(SuccessStatus._OK, keywords);
    }

    @Operation(summary = "내 키워드 선호 저장", description = "로그인 사용자의 키워드 선호를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/my/keywords")
    public ResponseEntity<ApiResponse<Void>> saveKeywordPreferences(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody KeywordRequest request
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        keywordService.setMyKeywords(userDetails.getMemberId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }

    @Operation(summary = "내 키워드 선호 조회", description = "로그인 사용자의 키워드 선호 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/my/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getMyKeywords(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        List<KeywordResponse> keywords = keywordService.getMyKeywords(userDetails.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus._OK, keywords);
    }

}
