package ru.profitsw2000.graphtab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.util.Pair
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import ru.profitsw2000.graphtab.R
import ru.profitsw2000.graphtab.databinding.FragmentDateSelectionBottomSheetBinding
import ru.profitsw2000.graphtab.presentation.viewmodel.GraphViewModel
import java.text.SimpleDateFormat
import java.util.Date

class DateSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDateSelectionBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val graphViewModel: GraphViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDateSelectionBottomSheetBinding.bind(
            inflater.inflate(R.layout.fragment_date_selection_bottom_sheet, container, false)
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
        setDateTextView(
            graphViewModel.getGraphFilterDatePeriod().first,
            graphViewModel.getGraphFilterDatePeriod().second
        )
        initDateChangeTextView()
        initButton()
    }

    private fun initButton() = with(binding) {
        applyFiltersButton.setOnClickListener {
            graphViewModel.setGraphFilterDatePeriod()
            this@DateSelectionBottomSheetFragment.dismiss()
        }
    }

    private fun initDateChangeTextView() = with(binding) {
        editDatePeriodTextView.setOnClickListener {
            selectDateRangeDialog()
        }
    }

    private fun setDateTextView(fromDate: Date?, toDate: Date?) = with(binding) {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

        datePeriodTextView.text = if (fromDate == null || toDate == null) "За всё время"
        else "${simpleDateFormat.format(fromDate)} - ${simpleDateFormat.format(toDate)}"
    }

    private fun selectDateRangeDialog() {
        val dateRangePicker = MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setTitleText(ru.profitsw2000.core.R.string.history_table_date_range_picker_dialog_title_text)
            .setPositiveButtonText(ru.profitsw2000.core.R.string.history_table_date_range_picker_dialog_positive_button_text)
            .setNegativeButtonText(ru.profitsw2000.core.R.string.message_cancel_button_text)
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
        dateRangePicker.show(childFragmentManager, "DATE_PICKER")
        dateRangePicker.addOnPositiveButtonClickListener {
            val fromDate = Date(it.first)
            val toDate = Date(it.second)
            setDateTextView(fromDate, toDate)
            graphViewModel.changeDatePeriod(fromDate, toDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}