package ru.profitsw2000.memoryfragment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.TOTAL_MEMORY_BYTE_SIZE
import ru.profitsw2000.data.model.state.MemoryScreenState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryClearState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryDataLoadState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryInfoState
import ru.profitsw2000.memoryfragment.R
import ru.profitsw2000.memoryfragment.databinding.FragmentMemoryBinding
import ru.profitsw2000.memoryfragment.presentation.viewmodel.MemoryViewModel

class MemoryFragment : Fragment() {

    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!
    private val memoryViewModel: MemoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) memoryViewModel.getMemoryInfo(viewLifecycleOwner.lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMemoryBinding.bind(inflater.inflate(R.layout.fragment_memory, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    private fun initViews() {
        binding.clearMemoryButton.setOnClickListener {
            memoryViewModel.clearMemory(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun observeData() {
        observeMemoryInfoData()
        observeMemoryClearData()
        observeMemoryLoadData()
    }

    private fun observeMemoryInfoData() {
        val observer = Observer<MemoryInfoState> { renderMemoryInfo(it) }
        memoryViewModel.memoryInfoLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun observeMemoryClearData() {
        val observer = Observer<MemoryClearState> { renderMemoryClearData(it) }
        memoryViewModel.memoryClearLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun observeMemoryLoadData() {
        val observer = Observer<MemoryDataLoadState> { renderMemoryLoadData(it) }
        memoryViewModel.memoryLoadLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderMemoryInfo(memoryInfoState: MemoryInfoState) {
        when(memoryInfoState) {
            MemoryInfoState.MemoryInfoInitialState -> {}
            MemoryInfoState.MemoryInfoDeviceConnectionError -> memoryInfoError(getString(ru.profitsw2000.core.R.string.device_connection_error_message_text))
            MemoryInfoState.MemoryInfoLoad -> setRoundProgressBarState(true)
            MemoryInfoState.MemoryInfoSendRequestError -> memoryInfoError(getString(ru.profitsw2000.core.R.string.request_sending_error_message_text))
            is MemoryInfoState.MemoryInfoSuccess -> populateMemoryInfoViews(
                memoryInfoState.memoryInfoModel.currentMemoryAddress,
                memoryInfoState.memoryInfoModel.memoryPercentUsage
            )
            MemoryInfoState.MemoryInfoTimeoutError -> memoryInfoError(getString(ru.profitsw2000.core.R.string.timeout_error_message_text))
        }
    }

    private fun renderMemoryClearData(memoryClearState: MemoryClearState) {
        when(memoryClearState) {
            MemoryClearState.MemoryClearInitialState -> {}
            MemoryClearState.MemoryClearExecution -> setRoundProgressBarState(true)
            MemoryClearState.MemoryClearSuccess -> memoryClearSuccess()
            MemoryClearState.MemoryClearDeviceConnectionError -> memoryClearError(getString(ru.profitsw2000.core.R.string.device_connection_error_message_text))
            MemoryClearState.MemoryClearError -> memoryClearError(getString(ru.profitsw2000.core.R.string.memory_clear_error_message_text))
            MemoryClearState.MemoryClearSendRequestError -> memoryClearError(getString(ru.profitsw2000.core.R.string.request_sending_error_message_text))
            MemoryClearState.MemoryClearTimeoutError -> memoryInfoError(getString(ru.profitsw2000.core.R.string.timeout_error_message_text))
        }
    }

    private fun renderMemoryLoadData(memoryDataLoadState: MemoryDataLoadState) {
        when(memoryDataLoadState) {
            MemoryDataLoadState.MemoryDataLoadDeviceConnectionError -> TODO()
            MemoryDataLoadState.MemoryDataLoadInitialState -> {}
            MemoryDataLoadState.MemoryDataLoadRequestError -> TODO()
            MemoryDataLoadState.MemoryDataLoadStopReceived -> TODO()
            MemoryDataLoadState.MemoryDataLoadStopRequest -> TODO()
            MemoryDataLoadState.MemoryDataLoadTimeoutError -> TODO()
            is MemoryDataLoadState.MemoryDataReceived -> TODO()
            is MemoryDataLoadState.MemoryDataRequest -> TODO()
            is MemoryDataLoadState.ServiceDataReceived -> TODO()
            MemoryDataLoadState.ServiceDataRequest -> TODO()
        }
    }

    private fun setRoundProgressBarState(isVisible: Boolean) = with(binding) {
        updateTimeButton.isEnabled = !isVisible
        updateTimeButton.isEnabled = !isVisible
        if (isVisible) roundProgressBar.visibility = View.VISIBLE
        else roundProgressBar.visibility = View.GONE
    }

    private fun memoryInfoError(message: String) {
        setRoundProgressBarState(false)
        showSimpleMessage(
            messageTitle = getString(ru.profitsw2000.core.R.string.error_message_title),
            messageText = message)
        memoryViewModel.setMemoryInfoToInitialState()
    }

    private fun memoryClearError(message: String) {
        setRoundProgressBarState(false)
        showSimpleMessage(
            messageTitle = getString(ru.profitsw2000.core.R.string.error_message_title),
            messageText = message)
        memoryViewModel.setMemoryClearToInitialState()
    }

    private fun showSimpleMessage(
        messageTitle: String,
        messageText: String
        ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setNeutralButton(ru.profitsw2000.core.R.string.ok_dialog_button_text) { dialog, _ -> dialog.dismiss()}
            .create()
            .show()
    }

    private fun populateMemoryInfoViews(
        currentAddress: Int,
        memoryPercentUsage: Float) = with(binding) {
        setRoundProgressBarState(false)
        memoryUsageValueTextView.text = getString(ru.profitsw2000.core.R.string.memory_volume_text, currentAddress)
        freeMemoryTitleTextView.text = getString(ru.profitsw2000.core.R.string.memory_volume_text, (TOTAL_MEMORY_BYTE_SIZE - currentAddress))
        memorySpaceIndicatorProgressBar.progress = currentAddress
        memorySpacePercentageTextView.text = "$memoryPercentUsage %"
        memoryViewModel.setMemoryInfoToInitialState()
    }

    private fun memoryClearSuccess() {
        setRoundProgressBarState(false)
        showSimpleMessage(
            messageTitle = getString(ru.profitsw2000.core.R.string.operation_completed_message_title),
            messageText = getString(ru.profitsw2000.core.R.string.clear_memory_completed_successfully))
        memoryViewModel.setMemoryClearToInitialState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}