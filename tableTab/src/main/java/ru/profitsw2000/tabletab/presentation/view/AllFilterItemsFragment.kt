package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.ALL_FILTER_ITEMS_KEY
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.getLettersFromCodeList
import ru.profitsw2000.data.model.state.filterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.filterscreen.LocalIdsLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState
import ru.profitsw2000.navigator.Navigator
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentAllFilterItemsBinding
import ru.profitsw2000.tabletab.presentation.viewmodel.FilterViewModel
import ru.profitsw2000.tabletab.utils.SensorDataAction

class AllFilterItemsFragment : Fragment() {

    @OptIn(ExperimentalStdlibApi::class)
    private val hexFormat = HexFormat {
        upperCase = true
        number.removeLeadingZeros = true
        number{ prefix = "0x" }
    }
    private var _binding: FragmentAllFilterItemsBinding? = null
    private val binding
        get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModel()
    private val navigator: Navigator by inject()
    private val sensorDataAction: SensorDataAction? by lazy {
        arguments?.getParcelable(ALL_FILTER_ITEMS_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllFilterItemsBinding.bind(inflater.inflate(R.layout.fragment_all_filter_items, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun initViews() {

    }

    private fun observeData() {
        when(sensorDataAction) {
            SensorDataAction.LetterCodeDataAction -> observeLetterCodeData()
            SensorDataAction.LocalIdDataAction -> observeLocalIdData()
            SensorDataAction.SerialNumberDataAction -> observeSerialNumberData()
            null -> {}
        }
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

    private fun renderSerialNumberData(sensorIdsLoadState: SensorIdsLoadState) {
        when(sensorIdsLoadState) {
            SensorIdsLoadState.Error -> {}
            SensorIdsLoadState.Loading -> {}
            is SensorIdsLoadState.Success -> createCheckBoxes(
                sensorIdsLoadState.sensorIdsList
            )
        }
    }

    private fun renderLocalIdData(localIdsLoadState: LocalIdsLoadState) {
        when(localIdsLoadState) {
            LocalIdsLoadState.Error -> {}
            LocalIdsLoadState.Loading -> {}
            is LocalIdsLoadState.Success -> createCheckBoxes(
                numberList = localIdsLoadState.localIdsList
            )
        }
    }

    private fun renderLetterCodeData(letterCodesLoadState: LetterCodesLoadState) {
        when(letterCodesLoadState) {
            LetterCodesLoadState.Error -> {}
            LetterCodesLoadState.Loading -> {}
            is LetterCodesLoadState.Success -> createCheckBoxes(
                getLettersFromCodeList(letterCodesLoadState.letterCodesList)
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <T> createCheckBoxes(numberList: List<T>) {

        if (numberList.isNotEmpty()) {
            numberList.forEach { item ->

                val contextThemeWrapper = ContextThemeWrapper(requireContext(), ru.profitsw2000.core.R.style.filterItemsCheckBoxStyle)
                val checkBoxesLinearLayout = binding.checkBoxesLinearLayout
                val checkBox = CheckBox(contextThemeWrapper)
                val view = View(requireContext())

                checkBox.apply {
                    text = when(item) {
                        is Int -> "${item.toHexString(hexFormat)}"
                        is Long -> "${item.toHexString(hexFormat)}"
                        else -> "${item.toString()}"
                    }
                    isChecked = when(item) {
                        is Int -> filterViewModel.checkedLocalIdList.contains(item)
                        is Long -> filterViewModel.checkedSensorNumberList.contains(item)
                        is String -> filterViewModel.checkedLetterList.contains(item)
                        else -> false
                    }
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) filterViewModel.addElementToCheckedList(item)
                        else filterViewModel.removeElementFromCheckedList(item)
                    }
                }
            }
        } else TODO("Убрать кнопку")
    }
}