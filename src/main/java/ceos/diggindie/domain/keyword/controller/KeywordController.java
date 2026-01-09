package ceos.diggindie.domain.keyword.controller;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.Response;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Operation(summary = "전체 키워드 조회 API", description = "서비스에서 제공하는 모든 키워드를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/keywords")
    public ResponseEntity<Response<List<KeywordResponse>>> getAllKeywords() {

        List<KeywordResponse> keywordResponses = keywordService.getAllKeywords();
        Response<List<KeywordResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                keywordResponses,
                "키워드 조회 API"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드 설정 API", description = "로그인 사용자의 키워드 선호를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/my/keywords")
    public ResponseEntity<Response<Void>> saveKeywordPreferences(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody KeywordRequest request
    ) {

        Response<Void> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                "키워드 설정 API"
        );
        keywordService.setMyKeywords(userDetails.getMemberId(), request);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "사용자 키워드 반환 API", description = "로그인 사용자의 키워드 선호 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my/keywords")
    public ResponseEntity<Response<List<KeywordResponse>>> getMyKeywords(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        List<KeywordResponse> keywordResponses = keywordService.getMyKeywords(userDetails.getMemberId());
        Response<List<KeywordResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                keywordResponses,
                "사용자 키워드 반환 API"
        );

        return ResponseEntity.ok(response);
    }

}
