package com.example.excel_ai_reporter.config;

import com.example.excel_ai_reporter.dto.OpenAiRequest;
import com.example.excel_ai_reporter.dto.OpenAiResponse;
import com.example.excel_ai_reporter.model.KpiAnalysisRow;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class OpenAiClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    public String generate(String promt){
        WebClient client=webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+apiKey)
                .build();

        OpenAiRequest req=new OpenAiRequest(model,promt);

        OpenAiResponse res=client.post()
                .uri("/v1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .block();

        if (res ==null) throw  new IllegalStateException("Openai response is empty");
        String text=res.firstTextOrnull();
        if(text==null || text.isBlank()) throw new IllegalStateException("Coud not found Openai response");
        return text.trim();
    }


}
