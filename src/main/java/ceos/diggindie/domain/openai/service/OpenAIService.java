package ceos.diggindie.domain.openai.service;

import ceos.diggindie.domain.openai.dto.OpenAIRequest;
import ceos.diggindie.domain.openai.dto.OpenAIResponse;
import ceos.diggindie.domain.openai.dto.PromptRequest;
import ceos.diggindie.domain.spotify.dto.SpotifySearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.client-id}")
    private String CLIENT_ID;

    @Value("${openai.model}")
    private String MODEL;

    private final RestClient restClient;

    public String callOpenAI(PromptRequest request) {

        OpenAIRequest openAIRequest = new OpenAIRequest(
                MODEL,
                request.prompt()
        );

        OpenAIResponse response = restClient
                .post()
                .uri("https://api.openai.com/v1/responses")
                .header("Authorization", "Bearer " + CLIENT_ID)
                .body(openAIRequest)
                .retrieve()
                .body(OpenAIResponse.class);

        if (response == null || response.output() == null || response.output().isEmpty()) {
            return null;
        }

        String result = response.output().stream()
                .filter(o -> o.content() != null)
                .flatMap(o -> o.content().stream())
                .filter(c -> c.text() != null)
                .map(OpenAIResponse.ContentItem::text)
                .collect(Collectors.joining("\n"));

        return result;
    }
}
