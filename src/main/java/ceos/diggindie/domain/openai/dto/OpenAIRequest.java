package ceos.diggindie.domain.openai.dto;

public record OpenAIRequest(
        String model,
        String input
    ) {
}
