package com.example.excel_ai_reporter.service;

import com.example.excel_ai_reporter.model.KpiAnalysisRow;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChartSevice {
    public byte[] createChangePctChart(List<KpiAnalysisRow> rows){
        try{

            // x axis -kpi names
            List<String> metrics=rows.stream()
                    .map(KpiAnalysisRow::getMetric)
                    .collect(Collectors.toList());

            // y axis-kpi change
            List<Double> values=rows.stream()
                    .map(r->r.getChangePct()== null ? 0 :r.getChangePct().doubleValue())
                    .collect(Collectors.toList());

            CategoryChart chart=new CategoryChartBuilder()
                    .width(900)
                    .height(500)
                    .title("KPI Change Percentage")
                    .xAxisTitle("Metric")
                    .yAxisTitle("Change %")
                    .build();
            // Style
            chart.getStyler().setLegendVisible(false);
            //chart.getStyler().setHasAnnotations(true);
            chart.getStyler().setPlotGridLinesVisible(true);
            chart.getStyler().setChartTitleVisible(true);
            chart.getStyler().setXAxisLabelRotation(45);
            chart.getStyler().setDefaultSeriesRenderStyle(
                    CategorySeries.CategorySeriesRenderStyle.Bar
            );
            // Data add
            chart.addSeries("Change %", metrics, values);
            // PNG output
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapEncoder.saveBitmap(chart, baos, BitmapEncoder.BitmapFormat.PNG);

            return baos.toByteArray();
        }catch (Exception e){
            throw new IllegalStateException("Chart oluşturulamadı", e);
        }
    }
}
