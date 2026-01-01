package ru.profitsw2000.graphtab.presentation.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentGraphBinding
import ru.profitsw2000.graphtab.presentation.view.utils.LineChartConfigurator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGraphBinding.bind(inflater.inflate(R.layout.fragment_graph, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChart()
    }

    private fun setupChart() = with(binding) {
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setDrawGridBackground(false)

        // Configure X axis (Date/Time)
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.LTGRAY
        xAxis.textColor = Color.BLACK
        xAxis.axisLineColor = Color.BLACK
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val dateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault())

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
        leftAxis.axisMinimum = 0f // Adjust based on your data range

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

        // Animation
        lineChart.animateX(1000)
    }


}