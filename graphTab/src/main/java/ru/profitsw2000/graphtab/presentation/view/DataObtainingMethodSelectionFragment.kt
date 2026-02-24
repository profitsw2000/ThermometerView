package ru.profitsw2000.graphtab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentDataObtainingMethodSelectionBinding
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel

class DataObtainingMethodSelectionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDataObtainingMethodSelectionBinding? = null
    private val binding get() = _binding!!
    private val graphViewModel: GraphViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDataObtainingMethodSelectionBinding.bind(
            inflater.inflate(R.layout.fragment_data_obtaining_method_selection, container, false)
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val layout: CoordinatorLayout = binding.rootCoordinatorLayout
        layout.minimumHeight = 500

        initViews()
    }

    private fun initViews() {
        initRadioGroup(graphViewModel.getTimeFrameDataObtainingMethodFilter())
        initButton()
    }

    private fun initRadioGroup(
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod
    ) = with(binding) {
        when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> intervalAverageValueRadioButton.isChecked = true
            TimeFrameDataObtainingMethod.TimeFrameBegin -> intervalBeginValueRadioButton.isChecked = true
            TimeFrameDataObtainingMethod.TimeFrameEnd -> intervalEndValueRadioButton.isChecked = true
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> intervalMaximumValueRadioButton.isChecked = true
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> intervalMinimumValueRadioButton.isChecked = true
        }
    }

    private fun initButton() = with(binding) {
        applyFiltersButton.setOnClickListener {
            graphViewModel.setTimeFrameDataObtainingMethodFilter(
                getSelectedRadioButton()
            )
            this@DataObtainingMethodSelectionFragment.dismiss()
        }
    }

    private fun getSelectedRadioButton(): TimeFrameDataObtainingMethod = with(binding) {
        val checkedRadioButtonId = temperatureValueCalculationMethodSelectionRadioGroup.checkedRadioButtonId

        return when(checkedRadioButtonId) {
            intervalBeginValueRadioButton.id -> TimeFrameDataObtainingMethod.TimeFrameBegin
            intervalEndValueRadioButton.id -> TimeFrameDataObtainingMethod.TimeFrameEnd
            intervalAverageValueRadioButton.id -> TimeFrameDataObtainingMethod.TimeFrameAverage
            intervalMaximumValueRadioButton.id -> TimeFrameDataObtainingMethod.TimeFrameMaximum
            intervalMinimumValueRadioButton.id -> TimeFrameDataObtainingMethod.TimeFrameMinimum
            else -> TimeFrameDataObtainingMethod.TimeFrameBegin
        }
    }
}