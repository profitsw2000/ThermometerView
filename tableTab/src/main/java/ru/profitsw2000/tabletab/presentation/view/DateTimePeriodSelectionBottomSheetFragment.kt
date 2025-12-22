package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentDateTimePeriodSelectionBottomSheetBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}