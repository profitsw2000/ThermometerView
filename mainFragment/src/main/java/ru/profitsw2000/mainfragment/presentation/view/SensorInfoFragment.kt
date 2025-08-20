package ru.profitsw2000.mainfragment.presentation.view

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.SENSOR_INDEX_BUNDLE
import ru.profitsw2000.core.utils.constants.SENSOR_INFO_DISMISS
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.data.model.SensorModel
import ru.profitsw2000.data.model.state.SensorInfoState
import ru.profitsw2000.mainfragment.R
import ru.profitsw2000.mainfragment.databinding.FragmentBluetoothPairedDevicesListBinding
import ru.profitsw2000.mainfragment.databinding.FragmentSensorInfoBinding
import ru.profitsw2000.mainfragment.presentation.viewmodel.SensorInfoViewModel
import java.util.Locale


class SensorInfoFragment : BottomSheetDialogFragment()  {

    private var _binding: FragmentSensorInfoBinding? = null
    private val binding get() = _binding!!
    private val sensorInfoViewModel: SensorInfoViewModel by viewModel()
    private var sensorIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sensorIndex = it.getInt(SENSOR_INDEX_BUNDLE, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSensorInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        val layout: CoordinatorLayout = binding.rootCoordinatorLayout
        layout.minimumHeight = 1500

        initViews()
        observeData()
        loadSensorInfo()
    }

    private fun initViews() = with(binding) {
        updateLetterButton.setOnClickListener {
            val letter = sensorLetterInputLayout.editText?.text.toString()
            if (sensorIndex >= 0) sensorInfoViewModel.updateLetter(sensorIndex, letter)
        }

        initEditTextView()
    }

    private fun observeData() {
        val observer = Observer<SensorInfoState> { renderData(it) }
        sensorInfoViewModel.sensorInfoLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(sensorInfoState: SensorInfoState) {
        when(sensorInfoState) {
            SensorInfoState.Error -> setErrorText()
            SensorInfoState.Loading -> loadingState()
            is SensorInfoState.Success -> populateView(sensorInfoState.sensorModel)
        }
    }

    private fun setErrorText() = with(binding) {
        mainViewsGroup.visibility = View.GONE
        updateSensorLetterGroup.visibility = View.GONE
        progressBar.visibility = View.GONE
        sensorInfoLoadErrorTextView.visibility = View.VISIBLE
    }

    private fun loadingState() = with(binding) {
        progressBar.visibility = View.VISIBLE
        sensorInfoLoadErrorTextView.visibility = View.GONE
    }

    private fun loadSensorInfo() {
        if (sensorIndex > -1) sensorInfoViewModel.startSensorInfoFlow(sensorIndex, viewLifecycleOwner.lifecycleScope)
        else setErrorText()
    }

    private fun populateView(sensorModel: SensorModel) = with(binding) {
        val romCodeString = getString(ru.profitsw2000.core.R.string.hex_string, sensorModel.sensorId.toString(16).uppercase(Locale.getDefault()))
        val localIdString = getString(ru.profitsw2000.core.R.string.hex_string, sensorModel.sensorLocalId.toString(16).uppercase(Locale.getDefault()))
        val temperatureString = getString(ru.profitsw2000.core.R.string.sensor_temperature_text, sensorModel.sensorTemperature.toString())

/*        Log.d(TAG, "romCodeString: $romCodeString")
        Log.d(TAG, "localIdString: $localIdString")
        Log.d(TAG, "temperatureString: $temperatureString")
        Log.d(TAG, "letter: ${sensorModel.sensorLetter}")*/

        mainViewsGroup.visibility = View.VISIBLE
        sensorInfoLoadErrorTextView.visibility = View.GONE
        progressBar.visibility = View.GONE

        sensorRomCodeTextView.text = romCodeString
        localIdTextView.text = localIdString
        sensorTemperatureTextView.text = temperatureString
        sensorLetterTextView.text = sensorModel.sensorLetter
    }

    private fun initEditTextView() = with(binding) {
        editHideTextView.text = getUnderLineString(getString(ru.profitsw2000.core.R.string.show_edit_sensor_letter_form_text))

        editHideTextView.setOnClickListener {
            if (updateSensorLetterGroup.visibility == View.VISIBLE) {
                updateSensorLetterGroup.visibility = View.GONE
                editHideTextView.text = getUnderLineString(getString(ru.profitsw2000.core.R.string.show_edit_sensor_letter_form_text))
            } else {
                updateSensorLetterGroup.visibility = View.VISIBLE
                editHideTextView.text = getUnderLineString(getString(ru.profitsw2000.core.R.string.hide_edit_sensor_letter_form_text))
            }
        }
    }

    private fun getUnderLineString(text: String): SpannableString {
        val spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)

        return spannableString
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(SENSOR_INFO_DISMISS, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}