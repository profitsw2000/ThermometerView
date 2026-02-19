package ru.profitsw2000.graphtab.presentation.view.utils

import android.content.Context
import android.graphics.Color
import android.view.GestureDetector
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
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
    val sensorHistoryData: List<List<SensorHistoryDataModel>>,
    val context: Context
) {
    private lateinit var marker: GraphMarkerView
    private val sensorsNumber: Int by lazy {
        sensorHistoryData.size
    }

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
        configureRightYAxis()
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

    }

    fun displayTemperatureData(
        sensorHistoryDataModelList: List<SensorHistoryDataModel>
    ) {
        // Always sort by date ascending for the chart
        val sortedList = sensorHistoryDataModelList.sortedBy { it.date.time }
        if (sensorHistoryDataModelList.isEmpty()) {
            lineChart.clear()
            lineChart.invalidate()
            return
        }
        setDataToChart(
            getChartDataSet(
                getEntriesList(
                    sortedList
                )
            )
        )
        sortedList.run {
            if (this.isNotEmpty()) configureViewPort(this)
        }
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
        if (sensorsNumber > 1) {
            rightAxis.setDrawGridLines(true)
            rightAxis.gridColor = Color.LTGRAY
            rightAxis.textColor = Color.BLACK
            rightAxis.axisLineColor = Color.DKGRAY
            rightAxis.setDrawLabels(true)
        } else rightAxis.isEnabled = false
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

    private fun getChartDataSet(entries: ArrayList<Entry>): LineDataSet {
        // Configure dataset appearance
        val dataSet = LineDataSet(entries, "Temperature (°C)")

        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.RED)
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

        sensorHistoryDataModelList.forEach { data ->
            val xValue = data.date.time.toFloat()
            val yValue = data.temperature.toFloat()
            entries.add(Entry(xValue, yValue))
        }
        return entries
    }

    private fun setDataToChart(lineDataSet: LineDataSet) {
        lineChart.data = LineData(lineDataSet)
    }


    private fun configureViewPort(
        sensorHistoryDataModelList: List<SensorHistoryDataModel>
    ) {
        val first = sensorHistoryDataModelList.first()
        val last = sensorHistoryDataModelList.last()
        // Adjust Y axis based on data
        val minTemp = sensorHistoryDataModelList.minOf { it.temperature }.toFloat()
        val maxTemp = sensorHistoryDataModelList.maxOf { it.temperature }.toFloat()
        val padding = (maxTemp - minTemp) * 0.1f // 10% padding

        lineChart.xAxis.axisMinimum = first.date.time.toFloat()
        lineChart.xAxis.axisMaximum = last.date.time.toFloat()
        lineChart.axisLeft.axisMinimum = minTemp - padding
        lineChart.axisLeft.axisMaximum = maxTemp + padding
    }

    private fun configureViewPort(
        sensorHistoryData: List<List<SensorHistoryDataModel>>
    ) {
        val first = sensorHistoryData[0].first()
        val last = sensorHistoryData[0].last()
        var minLeftTemp: Float? = sensorHistoryData[0].minOf { it.temperature }.toFloat() ?: null
        var maxLeftTemp: Float? = sensorHistoryData[0].maxOf { it.temperature }.toFloat() ?: null
        var minRightTemp: Float? = sensorHistoryData[1].minOf { it.temperature }.toFloat() ?: null
        var maxRightTemp: Float? = sensorHistoryData[1].maxOf { it.temperature }.toFloat() ?: null

        getExtremumValues(sensorHistoryData).forEachIndexed { index, pair ->
            if (index%2 == 0) {
                when {
                    maxLeftTemp == null -> maxLeftTemp = pair.first
                    maxLeftTemp < pair.first -> maxLeftTemp = pair.first
                }
                when {
                    minLeftTemp == null -> minLeftTemp = pair.second
                    minLeftTemp < pair.second -> minLeftTemp = pair.second
                }
            } else {
                when {
                    maxRightTemp == null -> maxRightTemp = pair.first
                    maxRightTemp < pair.first -> maxRightTemp = pair.first
                }
                when {
                    minRightTemp == null -> minRightTemp = pair.second
                    minRightTemp < pair.second -> minRightTemp = pair.second
                }
            }
        }

        lineChart.xAxis.axisMinimum = first.date.time.toFloat()
        lineChart.xAxis.axisMaximum = last.date.time.toFloat()
        if (minLeftTemp != null && maxLeftTemp != null) {
            val paddingLeft = (maxLeftTemp - minLeftTemp) * 0.1f
            lineChart.axisLeft.axisMinimum = minLeftTemp - paddingLeft
            lineChart.axisLeft.axisMaximum = maxLeftTemp + paddingLeft
        }
        if (minRightTemp != null && maxRightTemp != null) {
            val paddingRight = (maxRightTemp - minRightTemp) * 0.1f // 10% padding
            lineChart.axisRight.axisMinimum = minRightTemp - paddingRight
            lineChart.axisRight.axisMaximum = maxRightTemp - paddingRight
        }
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