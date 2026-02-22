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
            SensorIdsLoadState.Error -> TODO()
            SensorIdsLoadState.Loading -> TODO()
            is SensorIdsLoadState.Success -> TODO()
        }
    }

    private fun renderLetterCodeData(letterCodesLoadState: LetterCodesLoadState) {
        when(letterCodesLoadState) {
            LetterCodesLoadState.Error -> TODO()
            LetterCodesLoadState.Loading -> TODO()
            is LetterCodesLoadState.Success -> TODO()
        }
    }

    private fun handle
}