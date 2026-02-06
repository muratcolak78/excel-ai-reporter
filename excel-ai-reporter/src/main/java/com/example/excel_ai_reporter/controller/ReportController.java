package com.example.excel_ai_reporter.controller;

import com.example.excel_ai_reporter.excel.ExcelKpiParser;
import com.example.excel_ai_reporter.model.KpiAnalysisRow;
import com.example.excel_ai_reporter.model.KpiRow;
import com.example.excel_ai_reporter.model.KpiSummaryResponse;
import com.example.excel_ai_reporter.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {
    private final ReportService service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<KpiRow> upload(@RequestParam("file")MultipartFile file){
        System.out.println(file.getContentType());
        return  service.parseKpiExcel(file);
    }
    @PostMapping(value="/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<KpiAnalysisRow> analyze(@RequestParam("file") MultipartFile file){
        return  service.analyze(file);
    }
    @PostMapping(value = "/summary-local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public KpiSummaryResponse summarayLocal(@RequestParam("file") MultipartFile file){
        return service.summaryLocal(file);
    }
    @PostMapping(value="/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> pdf(@RequestParam("file") MultipartFile file,
                                      @RequestParam("companyName") String companyName,
                                      @RequestParam("preparerName") String preparerName,
                                      @RequestParam(value = "logo", required = false) MultipartFile logo,
                                      @RequestParam(value = "sign", required = false) MultipartFile sign) throws IOException {
        byte [] pdfBytes= service.pdfWithAiSummary(file, logo, sign, companyName, preparerName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kpi_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
