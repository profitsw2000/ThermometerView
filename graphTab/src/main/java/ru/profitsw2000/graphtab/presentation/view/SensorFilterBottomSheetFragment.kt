package ru.profitsw2000.graphtab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.profitsw2000.data.model.state.sensorfilterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.sensorfilterscreen.SensorIdsLoadState
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentSensorFilterBottomSheetDialogBinding
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel

class SensorFilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSensorFilterBottomSheetDialogBinding? = null
    private val binding
        get() = _binding!!
    private val graphViewModel: GraphViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSensorFilterBottomSheetDialogBinding.bind(
            inflater.inflate(
                R.layout.fragment_sensor_filter_bottom_sheet_dialog,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val layout: CoordinatorLayout = binding.rootCoordinatorLayout
        layout.minimumHeight = 500

        observeData()
        initViews()
    }

    private fun initViews() = with(binding) {
        applyFiltersButton.setOnClickListener {
            graphViewModel.setSensorIdsAndLettersFilters()
        }
    }

    private fun observeData() {
        observeSensorIdData()
        observeLetterCodeData()
    }

    private fun observeSensorIdData() {
        val observer = Observer<SensorIdsLoadState> { renderSensorIdData(it) }
        graphViewModel.sensorIdsListLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun observeLetterCodeData() {
        val observer = Observer<LetterCodesLoadState> { renderLetterCodeData(it) }
        graphViewModel.letterCodesListLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderSensorIdData(sensorIdsLoadState: SensorIdsLoadState) {
        when(sensorIdsLoadState) {
            SensorIdsLoadState.Error -> handleSensorIdsLoadError()
            SensorIdsLoadState.Loading -> binding.sensorIdLoadProgressBar.visibility = View.VISIBLE
            is SensorIdsLoadState.Success -> inflateSensorIdsChips(sensorIdsLoadState.sensorIdsList)
        }
    }

    private fun renderLetterCodeData(letterCodesLoadState: LetterCodesLoadState) {
        when(letterCodesLoadState) {
            LetterCodesLoadState.Error -> handleLetterCodesLoadError()
            LetterCodesLoadState.Loading -> binding.letterCodesLoadProgressBar.visibility = View.VISIBLE
            is LetterCodesLoadState.Success -> inflateLetterCodesChips(letterCodesLoadState.letterCodesList)
        }
    }

    private fun handleSensorIdsLoadError() = with(binding) {
        serialNumberSelectionChipGroup.visibility = View.GONE
        sensorIdLoadProgressBar.visibility = View.GONE
        sensorIdLoadErrorTextView.visibility = View.VISIBLE
    }

    private fun handleLetterCodesLoadError() = with(binding) {
        letterSelectionChipGroup.visibility = View.GONE
        letterCodesLoadProgressBar.visibility = View.GONE
        letterCodesLoadErrorTextView.visibility = View.VISIBLE
    }

    private fun inflateSensorIdsChips(sensorIdsList: List<Pair<Long, Boolean>>) = with(binding) {
        sensorIdLoadProgressBar.visibility = View.GONE
        if (sensorIdsList.isNotEmpty()) {
            sensorIdsList.forEach { sensorId ->
                val chip = Chip(requireContext())

                chip.text = sensorId.first.toString()
                chip.isChecked = sensorId.second
                chip.textSize = 12f
                chip.isCheckable = true
                chip.setOnClickListener {
                    letterSelectionChipGroup.clearCheck()
                    graphViewModel.addItemToSelectedSensorIdsList(sensorId.first)
                }
                serialNumberSelectionChipGroup.addView(chip)
            }
        }
    }

    private fun inflateLetterCodesChips(letterCodesList: List<Pair<Int, Boolean>>) = with(binding) {
        letterCodesLoadProgressBar.visibility = View.GONE
        if (letterCodesList.isNotEmpty()) {
            letterCodesList.forEach { letterCode ->
                val chip = Chip(requireContext())

                chip.text = letterCode.first.toString()
                chip.isChecked = letterCode.second
                chip.textSize = 12f
                chip.isCheckable = true
                chip.setOnClickListener {
                    serialNumberSelectionChipGroup.clearCheck()
                    graphViewModel.addItemToSelectedLetterCodesList(letterCode.first)
                }
                letterSelectionChipGroup.addView(chip)
            }
        }
    }

}