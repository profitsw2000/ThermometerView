package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.koin.android.ext.android.inject
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.getLettersFromCodeList
import ru.profitsw2000.data.model.state.filterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.filterscreen.LocalIdsLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState
import ru.profitsw2000.navigator.Navigator
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentHistoryTableFilterBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel
const val SECTION_ITEMS_MAXIMUM_NUMBER = 1

class HistoryTableFilterFragment : Fragment() {

    @OptIn(ExperimentalStdlibApi::class)
    private val hexFormat = HexFormat {
        upperCase = true
        number.removeLeadingZeros = true
        number{ prefix = "0x" }
    }

    private var _binding: FragmentHistoryTableFilterBinding? = null
    private val binding
        get() = _binding!!
    private val filterViewModel: FilterViewModel by inject()
    private val navigator: Navigator by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryTableFilterBinding.bind(inflater.inflate(R.layout.fragment_history_table_filter, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
        filterViewModel.loadFilterElements()
    }

    private fun observeData() {
        observeSerialNumberData()
        observeLocalIdData()
        observeLetterCodeData()
    }

    private fun observeSerialNumberData() {
        val observer = Observer<SensorIdsLoadState> { renderSerialNumberData(it) }
        filterViewModel.sensorIdsLoadLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun observeLocalIdData() {
        val observer = Observer<LocalIdsLoadState> { renderLocalIdData(it) }
        filterViewModel.localIdsLoadLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun observeLetterCodeData() {
        val observer = Observer<LetterCodesLoadState> { renderLetterCodeData(it) }
        filterViewModel.letterCodesLoadLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun initViews() = with(binding) {
        applyFiltersButton.setOnClickListener {
            navigator.navigateUp()
        }
        letterProgressBar.visibility = View.GONE
    }

    private fun setAllItemsTextClickListeners() = with(binding) {
        allSerialNumbersLinkText.setOnClickListener {
            TODO()
        }
        allLocalIdsLinkText.setOnClickListener {
            TODO()
        }
        allLettersLinkText.setOnClickListener {
            TODO()
        }
    }

    private fun renderSerialNumberData(sensorIdsLoadState: SensorIdsLoadState) {
        when(sensorIdsLoadState) {
            SensorIdsLoadState.Error -> {
                setViewVisibility(binding.serialNumberSectionGroup, false)
                setViewVisibility(binding.serialNumberProgressBar, false)
            }
            SensorIdsLoadState.Loading -> setViewVisibility(binding.serialNumberProgressBar, true)
            is SensorIdsLoadState.Success -> dataLoadSuccess(
                binding.allSerialNumbersLinkText,
                sensorIdsLoadState.sensorIdsList,
                binding.serialNumberProgressBar,
                binding.serialNumberSelectionChipGroup
            )
        }
    }

    private fun renderLocalIdData(localIdsLoadState: LocalIdsLoadState) {
        when(localIdsLoadState) {
            LocalIdsLoadState.Error -> {
                setViewVisibility(binding.localIdSectionGroup, false)
                setViewVisibility(binding.localIdProgressBar, false)
            }
            LocalIdsLoadState.Loading -> setViewVisibility(binding.localIdProgressBar, true)
            is LocalIdsLoadState.Success -> dataLoadSuccess(
                binding.allLocalIdsLinkText,
                localIdsLoadState.localIdsList,
                binding.localIdProgressBar,
                binding.localIdSelectionChipGroup
            )
        }
    }

    private fun renderLetterCodeData(letterCodesLoadState: LetterCodesLoadState) {
        when(letterCodesLoadState) {
            LetterCodesLoadState.Error -> {
                setViewVisibility(binding.letterSelectionSectionGroup, false)
                setViewVisibility(binding.letterProgressBar, false)
            }
            LetterCodesLoadState.Loading -> setViewVisibility(binding.letterProgressBar, true)
            is LetterCodesLoadState.Success -> dataLoadSuccess(
                binding.allLettersLinkText,
                    getLettersFromCodeList(letterCodesLoadState.letterCodesList),
                    binding.letterProgressBar,
                    binding.letterSelectionChipGroup
                )
        }
    }

    private fun <T> dataLoadSuccess(allItemsTextView: View,
                               numberList: List<T>,
                               progressBarView: ProgressBar,
                               chipGroup: ChipGroup) {
        if (numberList.size < SECTION_ITEMS_MAXIMUM_NUMBER) {
            inflateChips(numberList = numberList,
                progressBarView = progressBarView,
                chipGroup = chipGroup
            )
        } else {
            setViewVisibility(allItemsTextView, true)
            inflateChips(numberList = numberList.take(SECTION_ITEMS_MAXIMUM_NUMBER),
                progressBarView = progressBarView,
                chipGroup = chipGroup
            )
        }
    }

    private fun setViewVisibility(view: View, visible: Boolean) {
        if (visible) view.visibility = View.VISIBLE
        else view.visibility = View.GONE
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <T> inflateChips(numberList: List<T>,
                                          progressBarView: ProgressBar,
                                          chipGroup: ChipGroup) {
        setViewVisibility(progressBarView, false)
        var id = 0
        if (numberList.isNotEmpty()) {
            numberList.forEach { item ->
                val chip = Chip(requireContext())
                chip.text = when(item) {
                    is Int -> "${item.toHexString(hexFormat)}"
                    is Long -> "${item.toHexString(hexFormat)}"
                    else -> "${item.toString()}"
                }
                chip.setOnClickListener {
                    val clickedChip = it as Chip
                    if (clickedChip.isChecked) TODO()
                    else TODO()
                }
                chip.textSize = 12f
                chip.isCheckable = true
                chip.id = id
                id++
                chipGroup.addView(chip)
            }
        } else setViewVisibility(chipGroup, false)
    }
}