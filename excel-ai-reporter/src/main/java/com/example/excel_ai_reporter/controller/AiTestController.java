package com.example.excel_ai_reporter.controller;

import com.example.excel_ai_reporter.config.OpenAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiTestController {
    private final OpenAiClient openAiClient;
    @GetMapping("/test-openai")
    public String testOpenAi(@RequestParam(defaultValue = "Sales down 20%, Profit down 50%, Expenses up 30%. Write a short management summary.") String prpmt){
        return openAiClient.generate(prpmt);
    }

}
