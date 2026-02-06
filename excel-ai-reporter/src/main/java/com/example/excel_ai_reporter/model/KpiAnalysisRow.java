package com.example.excel_ai_reporter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiAnalysisRow {
    private String metric;
    private BigDecimal lastPeriod;
    private BigDecimal thisPeriod;

    private BigDecimal changeAbs;//this-last
    private BigDecimal changePct;//procent
    private String trend; // up-down-flat -na
    private String unit;
    private String notes;
}
