package com.example.excel_ai_reporter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class OpenAiRequest {
    private String model;
    private String input;
}
