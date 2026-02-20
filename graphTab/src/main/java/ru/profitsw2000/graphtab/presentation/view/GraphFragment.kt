package ru.profitsw2000.graphtab.presentation.view

import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentGraphBinding
import ru.profitsw2000.graphtab.presentation.view.utils.LineChartConfigurator
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel
import kotlin.math.abs

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding
        get() = _binding!!
    private val graphViewModel: GraphViewModel by viewModel()
    // Gesture detection
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val diffX = e2.x - (e1?.x ?: 0f)
                val diffY = e2.y - (e1?.y ?: 0f)
                val newItemsNumber = diffX/(binding.lineChart.width/50)
                // Only consider horizontal swipes
                if (abs(diffX) > abs(diffY) && !markerIsVisible) {
                    if (abs(diffX) > 100 && abs(velocityX) > 50) {
                        if (diffX > 0) {
                            // Swipe right - load older data
                            graphViewModel.loadData(newItemsNumber.toInt())
                        } else {
                            // Swipe left - load newer data
                            graphViewModel.loadData(newItemsNumber.toInt())
                        }
                        return true
                    }
                }
                return false
            }
        })
    }
    private val lineChartConfigurator: LineChartConfigurator by lazy {
        LineChartConfigurator(binding.lineChart, requireContext())
    }
    private var markerIsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        lineChartConfigurator.setupTouchListener(gestureDetector)
        lineChartConfigurator.setupChart()
        lineChartConfigurator.setChartMarker(markerIsVisible)
        observeData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.graph_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.graph_mode -> {
                markerIsVisible = !markerIsVisible
                lineChartConfigurator.setChartMarker(markerIsVisible)
                requireActivity().invalidateOptionsMenu()
                true
            }
            else -> true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (markerIsVisible)
            menu.findItem(R.id.graph_mode).setIcon(ru.profitsw2000.core.R.drawable.drag_mode)
        else
            menu.findItem(R.id.graph_mode).setIcon(ru.profitsw2000.core.R.drawable.marker_mode)
    }

    private fun observeData() {
        val observer = Observer<SensorHistoryDataLoadState> { renderData(it) }
        graphViewModel.sensorHistoryListLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(sensorHistoryDataLoadState: SensorHistoryDataLoadState) {
        when(sensorHistoryDataLoadState) {
            is SensorHistoryDataLoadState.Error -> {}
            SensorHistoryDataLoadState.Loading -> setProgressBarState(true)
            is SensorHistoryDataLoadState.Success -> {
                setProgressBarState(false)
                lineChartConfigurator.displayTemperatureData(
                    sensorHistoryDataLoadState.sensorHistoryDataModelList
                )
            }
        }
    }

    private fun setProgressBarState(isVisible: Boolean) = with(binding) {
        if (isVisible) progressBar.visibility = View.VISIBLE
        else progressBar.visibility = View.GONE
    }
}