package ceos.diggindie.domain.openai.dto;

import java.util.List;

public record OpenAIResponse(
        String id,
        String status,
        List<OutputItem> output
) {
    public record OutputItem(
            String id,
            String type,
            String status,
            String role,
            List<ContentItem> content
    ){}

    public record ContentItem(
            String type,
            String text
    ){}
}
