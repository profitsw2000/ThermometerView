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
import ru.profitsw2000.core.utils.constants.EIGHT_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.FOUR_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_DAY_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_HOUR_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.ONE_WEEK_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.THIRTY_MINUTES_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TWELVE_HOURS_FRAME_MILLIS
import ru.profitsw2000.core.utils.constants.TWO_HOURS_FRAME_MILLIS
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentTimeFrameFilterBottomSheetBinding
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel

class TimeFrameFilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTimeFrameFilterBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val graphViewModel: GraphViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTimeFrameFilterBottomSheetBinding.bind(
            inflater.inflate(R.layout.fragment_time_frame_filter_bottom_sheet, container, false)
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
        initChipGroup(graphViewModel.getTimeFrameFilter())
        initButton()
    }

    private fun initChipGroup(timeFrameMillis: Long) = with(binding) {
        when(timeFrameMillis) {
            TEN_MINUTES_FRAME_MILLIS -> tenMinChip.isChecked = true
            THIRTY_MINUTES_FRAME_MILLIS -> thirtyMinChip.isChecked = true
            ONE_HOUR_FRAME_MILLIS -> oneHourChip.isChecked = true
            TWO_HOURS_FRAME_MILLIS -> twoHoursChip.isChecked = true
            FOUR_HOURS_FRAME_MILLIS -> fourHoursChip.isChecked = true
            EIGHT_HOURS_FRAME_MILLIS -> eightHoursChip.isChecked = true
            TWELVE_HOURS_FRAME_MILLIS -> twelveHoursChip.isChecked = true
            ONE_DAY_FRAME_MILLIS -> dayChip.isChecked = true
            ONE_WEEK_FRAME_MILLIS -> weekChip.isChecked = true
            else -> tenMinChip.isChecked = true
        }
    }

    private fun getSelectedChip(): Long = with(binding) {
        val checkedChipId = timeIntervalSelectionChipGroup.checkedChipId

        return when(checkedChipId) {
            tenMinChip.id -> TEN_MINUTES_FRAME_MILLIS
            thirtyMinChip.id -> THIRTY_MINUTES_FRAME_MILLIS
            oneHourChip.id -> ONE_HOUR_FRAME_MILLIS
            twoHoursChip.id -> TWO_HOURS_FRAME_MILLIS
            fourHoursChip.id -> FOUR_HOURS_FRAME_MILLIS
            eightHoursChip.id -> EIGHT_HOURS_FRAME_MILLIS
            twelveHoursChip.id -> TWELVE_HOURS_FRAME_MILLIS
            dayChip.id -> ONE_DAY_FRAME_MILLIS
            weekChip.id -> ONE_WEEK_FRAME_MILLIS
            else -> TEN_MINUTES_FRAME_MILLIS
        }
    }

    private fun initButton() = with(binding) {
        applyFiltersButton.setOnClickListener {
            graphViewModel.setTimeFrameFilter(getSelectedChip())
            this@TimeFrameFilterBottomSheetFragment.dismiss()
        }
    }
}