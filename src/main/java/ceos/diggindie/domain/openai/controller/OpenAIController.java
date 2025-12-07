package ceos.diggindie.domain.openai.controller;

import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.openai.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService openAIService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/admin/openai")
    public String chat(
            @RequestBody PromptRequest prompt
    ) {
        return openAIService.callOpenAI(prompt);
    }

}
