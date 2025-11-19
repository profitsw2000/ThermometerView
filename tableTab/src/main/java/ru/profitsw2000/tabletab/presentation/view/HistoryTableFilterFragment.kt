package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
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
import ru.profitsw2000.tabletab.utils.SensorDataAction

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
            filterViewModel.getFilters()
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

    private fun getSectionGroup(sensorDataAction: SensorDataAction): Group = with(binding) {
        return when(sensorDataAction) {
            SensorDataAction.LetterCodeDataAction -> letterSelectionSectionGroup
            SensorDataAction.LocalIdDataAction -> localIdSectionGroup
            SensorDataAction.SerialNumberDataAction -> serialNumberSectionGroup
        }
    }

    private fun getProgressBar(sensorDataAction: SensorDataAction): ProgressBar = with(binding) {
        return when(sensorDataAction) {
            SensorDataAction.LetterCodeDataAction -> letterProgressBar
            SensorDataAction.LocalIdDataAction -> localIdProgressBar
            SensorDataAction.SerialNumberDataAction -> serialNumberProgressBar
        }
    }

    private fun getAllItemsTextView(sensorDataAction: SensorDataAction): TextView = with(binding) {
        return when(sensorDataAction) {
            SensorDataAction.LetterCodeDataAction -> allLettersLinkText
            SensorDataAction.LocalIdDataAction -> allLocalIdsLinkText
            SensorDataAction.SerialNumberDataAction -> allSerialNumbersLinkText
        }
    }

    private fun getChipGroup(sensorDataAction: SensorDataAction): ChipGroup = with(binding) {
        return when(sensorDataAction) {
            SensorDataAction.LetterCodeDataAction -> letterSelectionChipGroup
            SensorDataAction.LocalIdDataAction -> localIdSelectionChipGroup
            SensorDataAction.SerialNumberDataAction -> serialNumberSelectionChipGroup
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
                SensorDataAction.SerialNumberDataAction,
                sensorIdsLoadState.sensorIdsList
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
                sensorDataAction = SensorDataAction.LocalIdDataAction,
                numberList = localIdsLoadState.localIdsList
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
                sensorDataAction = SensorDataAction.LetterCodeDataAction,
                getLettersFromCodeList(letterCodesLoadState.letterCodesList)
            )
        }
    }

    private fun <T> dataLoadSuccess(
        sensorDataAction: SensorDataAction,
        numberList: List<T>
    ) {
        if (numberList.size < SECTION_ITEMS_MAXIMUM_NUMBER) {
            inflateChips(numberList = numberList,
                sensorDataAction = sensorDataAction
            )
        } else {
            setViewVisibility(getAllItemsTextView(sensorDataAction), true)
            inflateChips(numberList = numberList.take(SECTION_ITEMS_MAXIMUM_NUMBER),
                sensorDataAction = sensorDataAction
            )
        }
    }
    private fun setViewVisibility(view: View, visible: Boolean) {
        if (visible) view.visibility = View.VISIBLE
        else view.visibility = View.GONE
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <T> inflateChips(numberList: List<T>,
                                 sensorDataAction: SensorDataAction) {
        setViewVisibility(getProgressBar(sensorDataAction), false)

        if (numberList.isNotEmpty()) {
            numberList.forEach { item ->
                val chip = Chip(requireContext())
                chip.text = when(item) {
                    is Int -> "${item.toHexString(hexFormat)}"
                    is Long -> "${item.toHexString(hexFormat)}"
                    else -> "${item.toString()}"
                }
                chip.isChecked = when(item) {
                    is Int -> filterViewModel.checkedLocalIdList.contains(item)
                    is Long -> filterViewModel.checkedSensorNumberList.contains(item)
                    is String -> filterViewModel.checkedLetterList.contains(item)
                    else -> false
                }
                chip.setOnClickListener {
                    val clickedChip = it as Chip
                    Log.d(TAG, "inflateChips: ${clickedChip.isChecked}")
                    if (clickedChip.isChecked) filterViewModel.addElementToCheckedList(item)
                    else filterViewModel.removeElementFromCheckedList(item)
                }
                chip.textSize = 12f
                chip.isCheckable = true
                getChipGroup(sensorDataAction = sensorDataAction).addView(chip)
            }
        } else setViewVisibility(getSectionGroup(sensorDataAction), false)
    }
}