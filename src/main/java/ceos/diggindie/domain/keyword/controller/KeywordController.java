package ceos.diggindie.domain.keyword.controller;

import ceos.diggindie.common.config.security.CustomUserDetails;
import ceos.diggindie.common.exception.GeneralException;
import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.SuccessStatus;
import ceos.diggindie.domain.keyword.dto.KeywordRequest;
import ceos.diggindie.domain.keyword.dto.KeywordResponse;
import ceos.diggindie.domain.keyword.service.KeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getAllKeywords() {
        List<KeywordResponse> keywords = keywordService.getAllKeywords();
        return ApiResponse.onSuccess(SuccessStatus._OK, keywords);
    }

    @PostMapping("/my/keywords")
    public ResponseEntity<ApiResponse<Void>> saveKeywordPreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody KeywordRequest request
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        keywordService.setMyKeywords(userDetails.getMemberId(), request);
        return ApiResponse.onSuccess(SuccessStatus._CREATED);
    }

    @GetMapping("/my/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getMyKeywords(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) throw GeneralException.loginRequired();

        List<KeywordResponse> keywords = keywordService.getMyKeywords(userDetails.getMemberId());
        return ApiResponse.onSuccess(SuccessStatus._OK, keywords);
    }

}
