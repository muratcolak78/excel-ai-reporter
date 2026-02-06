package com.example.excel_ai_reporter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class AiReport {
    private String summary;
    private List<String> risks;
    private List<String> actions;
}
