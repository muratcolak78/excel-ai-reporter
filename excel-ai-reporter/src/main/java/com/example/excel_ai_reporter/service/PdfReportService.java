package com.example.excel_ai_reporter.service;

import com.example.excel_ai_reporter.model.KpiAnalysisRow;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfReportService {
    private final ChartSevice sevice;

    //create pdf byte code
    public byte[] buildPdf(String aitext, List<KpiAnalysisRow> rows, MultipartFile firmLogo, MultipartFile personSign,String companyName, String preparerName) throws IOException {

        // we are using output strem
        try(ByteArrayOutputStream baos=new ByteArrayOutputStream()){
            // create documet
            Document doc=new Document(PageSize.A4,36,36,36,36);
            PdfWriter.getInstance(doc,baos);
            doc.open();
            //add logo


            Image logo=loadImage(firmLogo);
            doc.add(logo);

            //title
            Font titleFont=new Font(Font.HELVETICA,16,Font.BOLD);
            Paragraph title=new Paragraph("AI KPI Management Report", titleFont);

            Paragraph company=new Paragraph(companyName, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            //doc.add(Chunk.NEWLINE);
            doc.add(company);
            doc.add(new Paragraph("Date: "+LocalDate.now()));
            //doc.add(Chunk.NEWLINE);

            //summary block
            // header
            Font hFont=new Font(Font.HELVETICA, 12, Font.BOLD);
            Paragraph head=new Paragraph("Executive Summary / Risiken /Maßnahmen", hFont);
            head.setAlignment(Element.ALIGN_CENTER);
            doc.add(head);
            doc.add(Chunk.NEWLINE);

            //body
            Font bFont=new Font(Font.HELVETICA, 11, Font.NORMAL);
            String bodytext=(aitext==null ? "No Ai summary":aitext);
            doc.add(new Paragraph(bodytext, bFont));


            //KPI table
            doc.add(new Paragraph("KPI Overview", hFont));
            doc.add(Chunk.NEWLINE);

            PdfPTable table=new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3.5f,1.5f,1.5f,1.5f,1.2f,1.8f});

            addHeader(table, "Metric");
            addHeader(table, "Last");
            addHeader(table, "This");
            addHeader(table, "Change %");
            addHeader(table, "Trend");
            addHeader(table, "Unit");

            for(KpiAnalysisRow r:rows){
                table.addCell(safe(r.getMetric()));
                table.addCell(safe(r.getLastPeriod()));
                table.addCell(safe(r.getThisPeriod()));
                BigDecimal pct=r.getChangePct();
                String pcrtext=pct==null ? "N/A" :r.getChangePct() + " %";
                table.addCell(coloredCell(pcrtext,pct));
                table.addCell(safe(r.getTrend()));
                table.addCell(safe(r.getUnit()));


            }
            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // add grafik/chart
            byte[] chartByte= sevice.createChangePctChart(rows);
            Image loadChart=loadChart(chartByte);
            doc.add(loadChart);
            doc.add(Chunk.NEWLINE);


            //footer
            //add sign
            Image sign=loadImage(personSign);
            doc.add(sign);
            doc.add(Chunk.NEWLINE);

            //add creater
            Font fFont=new Font(Font.HELVETICA,11,Font.NORMAL);
            doc.add(new Paragraph(preparerName, fFont));

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException("Pdf wurde nicht erstellt");
        }
    }
    private void addHeader(PdfPTable table, String text){
        Font font=new Font(Font.HELVETICA,10, Font.NORMAL);
        PdfPCell cell =new PdfPCell(new Phrase(text,font));
        cell.setBackgroundColor(new java.awt.Color(230,230,230));
        cell.setPadding(6f);
        table.addCell(cell);
    }
    private String safe(Object o){
        return o==null?"":String.valueOf(o);
    }


private Image loadImage(MultipartFile logoFile) {  // MultipartFile parametresi
    try {
        if (logoFile == null || logoFile.isEmpty()) {
            return null;  // Logo yoksa null dön
        }

        byte[] bytes = logoFile.getBytes();  // Direkt MultipartFile → bytes [web:50]

        Image image = Image.getInstance(bytes);  // Aynı satır!
        image.scaleToFit(120, 60);
        image.setAlignment(Image.ALIGN_LEFT);

        return image;

    } catch (Exception e) {
        throw new IllegalStateException("Image wurde nicht geloaded: " + e.getMessage());
    }
}

    private Image loadChart(byte [] chartBytes){
        try{
            Image chart=Image.getInstance(chartBytes);
            chart.scaleToFit(500,300);
            chart.setAlignment(Image.ALIGN_CENTER);
            return  chart;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PdfPCell coloredCell(String text, BigDecimal changePct) {
        Font font = new Font(Font.HELVETICA, 10, Font.NORMAL);

        if (changePct != null) {
            int val = changePct.intValue();
            if (val < 0) {
                font.setColor(Color.RED);
            } else if (val > 0) {
                font.setColor(new Color(0, 128, 0)); // koyu yeşil
            }
        }

        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5f);
        return cell;
    }




}
