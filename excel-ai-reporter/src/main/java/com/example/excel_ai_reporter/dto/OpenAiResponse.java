package com.example.excel_ai_reporter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data @JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiResponse {
    private List<OutputItem> output;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public  static class OutputItem{
        private String type;
        private List<ContentItem> content;
    }

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContentItem{
        private String type;
        private String text;
    }
    public String firstTextOrnull(){
        if(output== null ) return null;
        for(OutputItem oi :output){
            if(oi.getContent()==null) return null;
            for(ContentItem ci: oi.getContent()){
                if("output_text".equals(ci.getType()) && ci.getText()!=null){
                    return ci.getText();
                }
            }
        }
        return null;
    }
}
