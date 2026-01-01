package ru.profitsw2000.graphtab.presentation.view.utils

import com.github.mikephil.charting.charts.LineChart

class LineChartConfigurator(
    val lineChart: LineChart
) {

    fun configureLineChart() {
        lineChart.isDragEnabled = true
    }

}