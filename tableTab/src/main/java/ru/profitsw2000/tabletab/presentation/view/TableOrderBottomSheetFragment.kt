package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentTableOrderBottomSheetBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel

class TableOrderBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTableOrderBottomSheetBinding? = null
    private val binding
        get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTableOrderBottomSheetBinding.bind(inflater.inflate(R.layout.fragment_table_order_bottom_sheet, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        val layout: CoordinatorLayout = binding.rootCoordinatorLayout
        layout.minimumHeight = 1500

        initViews()
    }

    private fun initViews() = with(binding) {
        initRadioButtons()
        initApplyButton()
    }

    private fun initRadioButtons() = with(binding) {
        if (filterViewModel.isHistoryListOrderAscending()) orderSelectionRadioGroup.check(R.id.ascending_order_radio_button)
        else orderSelectionRadioGroup.check(R.id.descending_order_radio_button)
    }

    private fun initApplyButton() = with(binding) {
        when(orderSelectionRadioGroup.checkedRadioButtonId) {
            R.id.descending_order_radio_button -> filterViewModel.setHistoryListOrder(false)
            R.id.ascending_order_radio_button -> filterViewModel.setHistoryListOrder(true)
            else -> filterViewModel.setHistoryListOrder(false)
        }
        this@TableOrderBottomSheetFragment.dismiss()
    }
}