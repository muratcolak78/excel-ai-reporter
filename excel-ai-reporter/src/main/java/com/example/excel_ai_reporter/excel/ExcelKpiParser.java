package com.example.excel_ai_reporter.excel;

import com.example.excel_ai_reporter.model.KpiRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ExcelKpiParser {
    public List<KpiRow> parse(InputStream in){
        try(Workbook wb=new XSSFWorkbook(in)){
            Sheet sheet=wb.getSheet("KPI");
            if(sheet== null){
                sheet=wb.getSheetAt(0);
            }
            List<KpiRow> rows=new ArrayList<>();
            DataFormatter formatter=new DataFormatter(Locale.GERMANY);

            int lastRow=sheet.getLastRowNum();

            for(int r=1; r<=lastRow;r++){
                Row row= sheet.getRow(r);
                if(row==null) continue;

                String metric=safeString(formatter,row.getCell(0));
                if(metric== null||metric.isBlank()) continue;

                BigDecimal last=safeNumber(formatter, row.getCell(1));
                BigDecimal current=safeNumber(formatter,row.getCell(2));
                String unit=safeString(formatter, row.getCell(3));
                String notes=safeString(formatter, row.getCell(4));

                KpiRow kpiRow=new KpiRow();
                kpiRow.setMetric(metric.trim());
                kpiRow.setThisPeriod(current);
                kpiRow.setLastPeriod(last);
                kpiRow.setUnit(blankToNull(unit));
                kpiRow.setNotes(blankToNull(notes));

                rows.add(kpiRow);
            }
            return rows;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String safeString(DataFormatter formatter, Cell cell) {
        if (cell == null) return null;
        String s = formatter.formatCellValue(cell);
        return s == null ? null : s.trim();
    }
    private BigDecimal safeNumber(DataFormatter formatter, Cell cell){
        if(cell == null) return null;
        if(cell.getCellType()== CellType.NUMERIC){
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        String raw=formatter.formatCellValue(cell);
        if (raw== null) return null;
        raw=raw.trim();
        if (raw.isBlank()) return null;
        raw=raw.replace(" ", "");

        if(raw.contains(".")&& raw.contains(",")){
            raw=raw.replace(".","").replace(",",".");
        }else if(raw.contains(",")) {
            raw=raw.replace(",",".");
        }
        try {
            return new BigDecimal(raw);
        }catch (Exception e){
            throw new IllegalArgumentException("Number wurde nicht geparst --> "+raw);
        }



    }
    private String blankToNull(String s){
        return (s==null||s.isBlank()) ? null : s;
    }
}
