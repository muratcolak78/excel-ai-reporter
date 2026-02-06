package com.example.excel_ai_reporter.service;

import com.example.excel_ai_reporter.config.OpenAiClient;
import com.example.excel_ai_reporter.excel.ExcelKpiParser;
import com.example.excel_ai_reporter.model.KpiAnalysisRow;
import com.example.excel_ai_reporter.model.KpiRow;
import com.example.excel_ai_reporter.model.KpiSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ExcelKpiParser parser;
    private final OpenAiClient openAiClient;
    private final PdfReportService pdfReportService;

    public List<KpiRow> parseKpiExcel(MultipartFile file){
        if (file == null || file.isEmpty()) throw  new IllegalArgumentException("File is empty");
        String name=file.getOriginalFilename()==null ? "" : file.getOriginalFilename().toLowerCase();
        System.out.println("File name --> "+name);
        if(!name.endsWith(".xlsx")) throw new IllegalArgumentException("Only .xlsx files are accepted.");
        try{
            return  parser.parse(file.getInputStream());
        } catch (Exception e) {
            throw new IllegalArgumentException("File could not read :"+e.getMessage(), e);
        }
    }

    public List<KpiAnalysisRow> analyze(MultipartFile file){
        List<KpiRow> rows=parseKpiExcel(file);
        return rows.stream().map(r->{
            BigDecimal last=r.getLastPeriod();
            BigDecimal curr=r.getThisPeriod();
            BigDecimal abs=(last !=null && curr!=null)? curr.subtract(last):null; //fark
            BigDecimal pct=null; //  %
            String trend="NA";
            if(last!=null&& curr!=null){
                // compareTo BigDecimal'lerin sayı değerini karşılaştıran bir metoddur. ==, >, < operatörleri BigDecimal'de çalışmaz
                int cmp=curr.compareTo(last);
                trend=(cmp>0) ? "UP":(cmp<0 ) ? "DOWN" : "FLAT";

                // if last=0 is, we won't count procent
                int isLastZero=last.compareTo(BigDecimal.ZERO);

                if (isLastZero !=0){
                    pct=curr.subtract(last)
                            .divide(last,6, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2,RoundingMode.HALF_UP);

                }

            }
            return new KpiAnalysisRow(
                    r.getMetric(),
                    last,
                    curr,
                    abs,
                    pct,
                    trend,
                    r.getUnit(),
                    r.getNotes()
            );


        }).toList();
    }
    public KpiSummaryResponse summaryLocal(MultipartFile file){
    List<KpiAnalysisRow> rows=analyze(file);
    String promt=buildPrompt(rows);
    String airesponse=openAiClient.generate(promt);
    return new KpiSummaryResponse(airesponse);
    }

    private String buildPrompt(List<KpiAnalysisRow> rows) {

        StringBuilder sb = new StringBuilder();

        sb.append("Du bist ein erfahrener Business Analyst.\n");
        sb.append("Analysiere die folgenden KPI-Daten aus einem Excel-Report.\n");
        sb.append("Erstelle eine kurze Management-Zusammenfassung.\n\n");

        sb.append("KPI Veränderungen:\n");

        for (KpiAnalysisRow r : rows) {
            sb.append("- ")
                    .append(r.getMetric())
                    .append(": ")
                    .append(r.getChangePct())
                    .append("% ")
                    .append(r.getTrend())
                    .append("\n");
        }

        sb.append("\nBitte liefere:\n");
        sb.append("1. Eine kurze Executive Summary (max. 120 Wörter)\n");
        sb.append("2. Die 5 wichtigsten Risiken\n");
        sb.append("3. Die 5 wichtigsten Handlungsempfehlungen\n");

        sb.append("\nSchreibe präzise, management-orientiert und datenbasiert.");

        return sb.toString();
    }

    public byte[] pdfWithAiSummary(MultipartFile file,MultipartFile logo,MultipartFile sign, String companyName, String preparerName) throws IOException {
        List<KpiAnalysisRow> rows=analyze(file);
        String promt=buildPrompt(rows);
        String aiText= openAiClient.generate(promt);
        return pdfReportService.buildPdf(aiText,rows, logo, sign, companyName, preparerName);
    }


}
