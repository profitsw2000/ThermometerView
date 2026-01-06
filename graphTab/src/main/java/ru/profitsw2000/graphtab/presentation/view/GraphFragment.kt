package ru.profitsw2000.graphtab.presentation.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.ONE_HOUR_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.view.GraphMarkerView
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentGraphBinding
import ru.profitsw2000.graphtab.presentation.view.utils.LineChartConfigurator
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding
        get() = _binding!!
    private val graphViewModel: GraphViewModel by viewModel()
    private lateinit var marker: GraphMarkerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        graphViewModel.setCoroutineScope(viewLifecycleOwner.lifecycleScope)
        _binding = FragmentGraphBinding.bind(inflater.inflate(R.layout.fragment_graph, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupTouchListener()
        setupChart()
        observeData()
        graphViewModel.loadData()
    }

    private fun observeData() {
        val observer = Observer<SensorHistoryDataLoadState> { renderData(it) }
        graphViewModel.sensorHistoryListLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(sensorHistoryDataLoadState: SensorHistoryDataLoadState) {
        when(sensorHistoryDataLoadState) {
            is SensorHistoryDataLoadState.Error -> {}
            SensorHistoryDataLoadState.Loading -> {}
            is SensorHistoryDataLoadState.Success -> displayTemperatureData(
                sensorHistoryDataLoadState.sensorHistoryDataModelList
            )
        }
    }

    private fun setupChart() = with(binding) {
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setDrawGridBackground(false)
        lineChart.performClick()

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

        // Configure left Y axis (Temperature)
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.textColor = Color.BLACK
        leftAxis.axisLineColor = Color.BLACK
        leftAxis.axisMinimum = -12f // Adjust based on your data range
        leftAxis.axisMaximum = -6f // Adjust based on your data range
        leftAxis.setDrawLabels(true)

        // Configure right Y axis
        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        // Description
        lineChart.description.isEnabled = true
        lineChart.description.text = "Temperature Over Time"
        lineChart.description.textColor = Color.BLACK

        // Legend
        lineChart.legend.isEnabled = true
        lineChart.legend.textColor = Color.BLACK

        //Marker
        marker = GraphMarkerView(requireContext(), ru.profitsw2000.core.R.layout.graph_marker_view)
        marker.chartView = lineChart
        lineChart.marker = marker

        // Animation
        //lineChart.animateX(1000)
    }

    private fun displayTemperatureData(
        sensorHistoryDataModelList: List<SensorHistoryDataModel>
    ) = with(binding) {
        val entries = ArrayList<Entry>()

        sensorHistoryDataModelList.asReversed().forEach { data ->
            // Convert timestamp to float for X axis
            val xValue = data.date.time.toFloat()
            val yValue = data.temperature.toFloat()
            entries.add(Entry(xValue, yValue))
        }

        // Create dataset
        val dataSet = LineDataSet(entries, "Temperature (°C)")

        // Configure dataset appearance
        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setCircleColor(Color.RED)
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false) // Hide values on points
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth line
        dataSet.cubicIntensity = 0.2f

        // Create line data and set to chart
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configure viewport
        val first = sensorHistoryDataModelList.first()
        val last = sensorHistoryDataModelList.last()
        lineChart.xAxis.axisMinimum = last.date.time.toFloat()
        lineChart.xAxis.axisMaximum = first.date.time.toFloat()

        // Refresh chart
        lineChart.invalidate()
    }
}