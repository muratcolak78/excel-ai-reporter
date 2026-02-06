package com.example.excel_ai_reporter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiRow {
    private String metric;
    private BigDecimal lastPeriod;
    private BigDecimal thisPeriod;
    private String unit;   // optional
    private String notes;  // optional
}