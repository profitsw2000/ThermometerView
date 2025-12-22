package ru.profitsw2000.tabletab.presentation.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.SENSOR_INFO_DISMISS
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentTableOrderBottomSheetBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel

class TableOrderBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTableOrderBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        applyOrderButton.setOnClickListener {
            val selectedId = orderSelectionRadioGroup.checkedRadioButtonId

            when(selectedId) {
                R.id.descending_order_radio_button -> filterViewModel.setHistoryListOrder(false)
                R.id.ascending_order_radio_button -> filterViewModel.setHistoryListOrder(true)
                else -> filterViewModel.setHistoryListOrder(false)
            }
            this@TableOrderBottomSheetFragment.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}