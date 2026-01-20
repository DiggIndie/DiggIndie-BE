package ceos.diggindie.domain.openai.controller;

import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.openai.service.OpenAIService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "OpenAI", description = "OpenAI 관련 API (백엔드 내부용)")
@RestController
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "OpenAI 채팅 요청 [내부용]", description = "OpenAI API를 호출하여 응답을 받습니다. ADMIN 권한 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 요청 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (ADMIN만 접근 가능)")
    })
    @PostMapping("/api/admin/openai")
    public String chat(
            @RequestBody(description = "프롬프트 요청") @org.springframework.web.bind.annotation.RequestBody PromptRequest prompt
    ) {
        return openAIService.callOpenAI(prompt);
    }

}
