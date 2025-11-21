package ru.profitsw2000.tabletab.presentation.view

import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.ALL_FILTER_ITEMS_KEY
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAllFilterItemsBinding.bind(inflater.inflate(R.layout.fragment_all_filter_items, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sensorDataAction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable(ALL_FILTER_ITEMS_KEY, SensorDataAction::class.java)
        else arguments?.getParcelable(ALL_FILTER_ITEMS_KEY)
        setToolbarLabel(sensorDataAction = sensorDataAction)
        observeData(sensorDataAction = sensorDataAction)
        initViews()
    }

    private fun initViews() = with(binding) {
        applyFiltersButton.setOnClickListener {
            navigator.navigateUp()
        }
    }

    private fun observeData(sensorDataAction: SensorDataAction?) {
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
            SensorIdsLoadState.Error -> setErrorState()
            SensorIdsLoadState.Loading -> setLoadingState()
            is SensorIdsLoadState.Success -> createCheckBoxes(
                sensorIdsLoadState.sensorIdsList
            )
        }
    }

    private fun renderLocalIdData(localIdsLoadState: LocalIdsLoadState) {
        when(localIdsLoadState) {
            LocalIdsLoadState.Error -> setErrorState()
            LocalIdsLoadState.Loading -> setLoadingState()
            is LocalIdsLoadState.Success -> createCheckBoxes(
                numberList = localIdsLoadState.localIdsList
            )
        }
    }

    private fun renderLetterCodeData(letterCodesLoadState: LetterCodesLoadState) {
        when(letterCodesLoadState) {
            LetterCodesLoadState.Error -> setErrorState()
            LetterCodesLoadState.Loading -> setLoadingState()
            is LetterCodesLoadState.Success -> createCheckBoxes(
                getLettersFromCodeList(letterCodesLoadState.letterCodesList)
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun <T> createCheckBoxes(numberList: List<T>) {

        setSuccessState()

        if (numberList.isNotEmpty()) {
            numberList.forEach { item ->

                val contextThemeWrapper = ContextThemeWrapper(requireContext(), ru.profitsw2000.core.R.style.filterItemsCheckBoxStyle)
                val checkBoxesLinearLayout = binding.checkBoxesLinearLayout
                val checkBox = CheckBox(contextThemeWrapper)
                val view = View(requireContext()).apply {
                    setBackgroundResource(ru.profitsw2000.core.R.color.white_aluminium)
                }

                checkBox.apply {
                    text = when(item) {
                        is Int -> item.toHexString(hexFormat)
                        is Long -> item.toHexString(hexFormat)
                        else -> item.toString()
                    }
                    isChecked = when(item) {
                        is Int -> filterViewModel.checkedLocalIdList.contains(item)
                        is Long -> filterViewModel.checkedSensorNumberList.contains(item)
                        is String -> filterViewModel.checkedLetterList.contains(item)
                        else -> false
                    }
                    buttonDrawable = StateListDrawable()
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) filterViewModel.addElementToCheckedList(item)
                        else filterViewModel.removeElementFromCheckedList(item)
                    }
                }
                checkBoxesLinearLayout.addView(checkBox)
                checkBoxesLinearLayout.addView(view)
                view.layoutParams.height = resources.getDimension(ru.profitsw2000.core.R.dimen.field_title_divider_height_size).toInt()
            }
        } else binding.applyFiltersButton.visibility = View.GONE
    }

    private fun setVisibility(view: View, isVisible: Boolean) {
        if (isVisible) view.visibility = View.VISIBLE
        else view.visibility = View.GONE
    }

    private fun setVisibility(viewList: List<View>, isVisibleList: List<Boolean>) {
        viewList.forEachIndexed { index, view ->
            setVisibility(view = view, isVisibleList[index])
        }
    }

    private fun setErrorState() {
        setVisibility(
            arrayListOf(binding.filterElementsLoadErrorTextView,
                binding.applyFiltersButton,
                binding.progressBar),
            arrayListOf(true,false,false)
        )
    }

    private fun setLoadingState() {
        setVisibility(
            arrayListOf(binding.filterElementsLoadErrorTextView,
                binding.applyFiltersButton,
                binding.progressBar),
            arrayListOf(false,false,true)
        )
    }

    private fun setSuccessState() {
        setVisibility(
            arrayListOf(binding.filterElementsLoadErrorTextView,
                binding.applyFiltersButton,
                binding.progressBar),
            arrayListOf(false,true,false)
        )
    }

    private fun setToolbarLabel(sensorDataAction: SensorDataAction?) {
        val titleText = when(sensorDataAction) {
            SensorDataAction.SerialNumberDataAction -> resources.getString(ru.profitsw2000.core.R.string.serial_number_title_text)
            SensorDataAction.LocalIdDataAction -> resources.getString(ru.profitsw2000.core.R.string.sensor_local_id_selection_title_text)
            SensorDataAction.LetterCodeDataAction -> resources.getString(ru.profitsw2000.core.R.string.letter_section_title_text)
            null -> ""
        }
        (activity as? AppCompatActivity)?.supportActionBar?.title = titleText
    }
}