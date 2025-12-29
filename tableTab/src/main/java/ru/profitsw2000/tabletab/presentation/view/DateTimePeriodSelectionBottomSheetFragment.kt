package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentDateTimePeriodSelectionBottomSheetBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel
import java.util.Date

class DateTimePeriodSelectionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDateTimePeriodSelectionBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDateTimePeriodSelectionBottomSheetBinding.bind(
            inflater.inflate(
                R.layout.fragment_date_time_period_selection_bottom_sheet,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        val layout: CoordinatorLayout = binding.rootCoordinatorLayout
        layout.minimumHeight = 1500

        observeDateRangeLiveData()
        initViews()
    }

    private fun observeDateRangeLiveData() {
        val observer = Observer<String> { binding.datePeriodTextView.text = it }
        filterViewModel.dateRangeStringLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun initViews() = with(binding) {
        editDatePeriodTextView.setOnClickListener {
            selectDateRangeDialog()
        }
    }

    private fun selectDateRangeDialog() {
        val dateRangePicker = MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setTitleText("Выбрать временной период")
            .setPositiveButtonText("Выбрать")
            .setNegativeButtonText("Отмена")
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

            filterViewModel.setFilterDateRange(fromDate = fromDate, toDate = toDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}