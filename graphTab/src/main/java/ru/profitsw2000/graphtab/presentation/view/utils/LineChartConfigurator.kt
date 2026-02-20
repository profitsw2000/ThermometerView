package ru.profitsw2000.graphtab.presentation.view.utils

import android.content.Context
import android.graphics.Color
import android.view.GestureDetector
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.profitsw2000.core.view.GraphMarkerView
import ru.profitsw2000.data.model.SensorHistoryDataModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LineChartConfigurator(
    val lineChart: LineChart,
    val context: Context
) {
    private lateinit var marker: GraphMarkerView
    private val colors = listOf(
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.YELLOW,
        Color.MAGENTA,
        Color.BLACK
    )

    fun setupTouchListener(gestureDetector: GestureDetector) {
        lineChart.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            // Allow chart to still handle its own touch events
            false
        }
    }

    fun setupChart() {
        setChartBehaviour()
        configureXAxis()
        configureLeftYAxis()
        //configureRightYAxis()
        setChartDescription()
        setChartLegend()
        //setAnimation(1000)
    }

    fun setChartMarker(isVisible: Boolean) {
        //Marker
        marker = GraphMarkerView(context, ru.profitsw2000.core.R.layout.graph_marker_view)
        marker.chartView = lineChart
        if (isVisible) lineChart.marker = marker
        else lineChart.marker = null
    }

    fun displayTemperatureData(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        val dataSets = mutableListOf<LineDataSet>()
        if (sensorHistoryData.isEmpty()){
            lineChart.clear()
            lineChart.invalidate()
            return
        }

        sensorHistoryData.forEachIndexed { index, models ->
            val entries = getEntriesList(models)
            val dataSet = getChartDataSet(entries, colors[index%6])

            if (index%2 == 0) {
                dataSet.axisDependency = YAxis.AxisDependency.LEFT
            } else {
                dataSet.axisDependency = YAxis.AxisDependency.RIGHT
            }
            dataSets.add(dataSet)
        }

        configureViewPort(sensorHistoryData)
        lineChart.data = LineData(dataSets.toList())
        lineChart.invalidate()
    }

    private fun setChartBehaviour() {
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setDrawGridBackground(false)
        lineChart.performClick()
    }

    private fun configureXAxis() {
        // Configure X axis (Date/Time)
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.LTGRAY
        xAxis.textColor = Color.BLACK
        xAxis.axisLineColor = Color.BLACK
        xAxis.setDrawLabels(true)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 10*60f*1000f
        xAxis.setLabelCount(5, true)
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("HH:mm dd.MM", Locale.getDefault())

            override fun getFormattedValue(value: Float): String {
                val timestamp = value.toLong()
                return dateFormat.format(Date(timestamp))
            }
        }
    }

    private fun configureLeftYAxis() {
        // Configure left Y axis (Temperature)
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.textColor = Color.BLACK
        leftAxis.axisLineColor = Color.BLACK
        leftAxis.setDrawLabels(true)
    }

    private fun configureRightYAxis() {
        // Configure right Y axis
        val rightAxis = lineChart.axisRight

        rightAxis.setDrawGridLines(true)
        rightAxis.gridColor = Color.LTGRAY
        rightAxis.textColor = Color.BLACK
        rightAxis.axisLineColor = Color.DKGRAY
        rightAxis.setDrawLabels(true)
    }

    private fun setChartDescription() {
        // Description
        lineChart.description.isEnabled = true
        lineChart.description.text = "Temperature Over Time"
        lineChart.description.textColor = Color.BLACK
    }

    private fun setChartLegend() {
        // Legend
        lineChart.legend.isEnabled = true
        lineChart.legend.textColor = Color.BLACK
    }

    private fun setAnimation(duration: Int) {
        // Animation
        lineChart.animateX(duration)
    }

    private fun getChartDataSet(entries: ArrayList<Entry>, color: Int): LineDataSet {
        // Configure dataset appearance
        val dataSet = LineDataSet(entries, "Temperature (°C)")

        dataSet.color = color
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(color)
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f

        return dataSet
    }

    private fun getEntriesList(
        sensorHistoryDataModelList: List<SensorHistoryDataModel>
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        sensorHistoryDataModelList.sortedBy { it.date.time }.forEach { data ->
            val xValue = data.date.time.toFloat()
            val yValue = data.temperature.toFloat()
            entries.add(Entry(xValue, yValue))
        }
        return entries
    }

    private fun configureViewPort(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        if (sensorHistoryData.isNotEmpty()) {
            configureBottomViewPort(sensorHistoryData)
            configureLeftViewPort(sensorHistoryData)
        }
        if (sensorHistoryData.size > 1) {
            configureRightYAxis()
            configureRightViewPort(sensorHistoryData)
        }

    }

    private fun configureLeftViewPort(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        var minTemp: Float = sensorHistoryData[0].minOf { it.temperature }.toFloat()
        var maxTemp: Float = sensorHistoryData[0].maxOf { it.temperature }.toFloat()

        getExtremumValues(sensorHistoryData).forEachIndexed { index, pair ->
            if (index%2 == 0) {
                if (maxTemp < pair.first) maxTemp = pair.first
                if (minTemp > pair.second) minTemp = pair.second
            }
        }
        val paddingLeft = (maxTemp - minTemp) * 0.1f
        lineChart.axisLeft.axisMinimum = minTemp - paddingLeft
        lineChart.axisLeft.axisMaximum = maxTemp + paddingLeft
    }

    private fun configureRightViewPort(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        var minTemp: Float = sensorHistoryData[1].minOf { it.temperature }.toFloat()
        var maxTemp: Float = sensorHistoryData[1].maxOf { it.temperature }.toFloat()

        getExtremumValues(sensorHistoryData).forEachIndexed { index, pair ->
            if (index%2 == 1) {
                if (maxTemp < pair.first) maxTemp = pair.first
                if (minTemp > pair.second) minTemp = pair.second
            }
        }
        val paddingLeft = (maxTemp - minTemp) * 0.1f
        lineChart.axisRight.axisMinimum = minTemp - paddingLeft
        lineChart.axisRight.axisMaximum = maxTemp + paddingLeft
    }

    private fun configureBottomViewPort(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        lineChart.xAxis.axisMinimum = sensorHistoryData[0].first().date.time.toFloat()
        lineChart.xAxis.axisMaximum = sensorHistoryData[0].last().date.time.toFloat()
    }

    private fun getExtremumValues(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ): List<Pair<Float, Float>> {
        val extremumValuesList = mutableListOf<Pair<Float, Float>>()
        sensorHistoryData.forEach {
            extremumValuesList.add(
                Pair(
                    it.maxOf { it.temperature }.toFloat(),
                    it.minOf { it.temperature }.toFloat()
                )
            )
        }
        return extremumValuesList
    }
}