package ru.profitsw2000.memoryfragment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.TOTAL_MEMORY_BYTE_SIZE
import ru.profitsw2000.data.model.state.memoryscreen.MemoryClearState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryDataLoadState
import ru.profitsw2000.data.model.state.memoryscreen.MemoryInfoState
import ru.profitsw2000.memoryfragment.R
import ru.profitsw2000.memoryfragment.databinding.FragmentMemoryBinding
import ru.profitsw2000.memoryfragment.presentation.utils.MemoryDataLoadAction
import ru.profitsw2000.memoryfragment.presentation.utils.MemoryDataLoadAction.*
import ru.profitsw2000.memoryfragment.presentation.viewmodel.MemoryViewModel
import ru.profitsw2000.navigator.Navigator
import kotlin.getValue

class MemoryFragment : Fragment() {

    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!
    private val memoryViewModel: MemoryViewModel by viewModel()
    private val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleQuitButtonPress()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (savedInstanceState == null) memoryViewModel.getMemoryInfo(viewLifecycleOwner.lifecycleScope)
        _binding = FragmentMemoryBinding.bind(inflater.inflate(R.layout.fragment_memory, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    private fun handleQuitButtonPress() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!memoryViewModel.isDataExchange()) navigator.navigateUp()
                else showTwoButtonDialog(
                    getString(ru.profitsw2000.core.R.string.memory_data_load_completed_message_title),
                    getString(ru.profitsw2000.core.R.string.memory_data_exchange_quit_message_text),
                    getString(ru.profitsw2000.core.R.string.error_message_skip_button_text),
                    getString(ru.profitsw2000.core.R.string.message_cancel_button_text),
                    QuitMemoryLoad
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initViews() {

        binding.clearMemoryButton.setOnClickListener {
            showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.clear_memory_warning_message_title),
                getString(ru.profitsw2000.core.R.string.clear_memory_warning_message_text),
                getString(ru.profitsw2000.core.R.string.message_continue_button_text),
                getString(ru.profitsw2000.core.R.string.message_cancel_button_text),
                ClearMemory
            )
        }

        binding.loadMemoryDataButton.setOnClickListener {
            val messageText = if (binding.confirmDataDeletionCheckbox.isChecked) getString(ru.profitsw2000.core.R.string.start_memory_data_load_and_clear_message_text)
            else getString(ru.profitsw2000.core.R.string.start_memory_data_load_message_text)

            val positiveButtonText = if (binding.confirmDataDeletionCheckbox.isChecked) getString(ru.profitsw2000.core.R.string.message_continue_button_text)
            else getString(ru.profitsw2000.core.R.string.confirm_start_memory_data_load_message_button_text)

            showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.memory_data_load_completed_message_title),
                messageText,
                positiveButtonText,
                getString(ru.profitsw2000.core.R.string.message_cancel_button_text),
                StartMemoryLoad(binding.confirmDataDeletionCheckbox.isChecked)
            )
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
            MemoryDataLoadState.MemoryDataLoadInitialState -> setMemoryLoadProgressState(false)
            MemoryDataLoadState.ServiceDataRequest -> setProgressIndicator(
                0f,
                getString(ru.profitsw2000.core.R.string.service_data_request_status_text)
            )
            is MemoryDataLoadState.ServiceDataReceived -> requestFirstMemoryDataPacket(
                memoryDataLoadState.sensorsNumber,
                memoryDataLoadState.currentAddress
            )
            is MemoryDataLoadState.InvalidMemoryData -> showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.error_message_title),
                if (memoryDataLoadState.prevMemoryDataLoadState == MemoryDataLoadState.MemoryDataLoadClearRequest)
                    getString(ru.profitsw2000.core.R.string.invalid_memory_data_clear_received_error_text)
                else getString(ru.profitsw2000.core.R.string.invalid_memory_data_received_error_text),
                getString(ru.profitsw2000.core.R.string.message_continue_button_text),
                getString(ru.profitsw2000.core.R.string.error_message_skip_button_text),
                ContinueMemoryLoad(memoryDataLoadState.prevMemoryDataLoadState)
            )
            is MemoryDataLoadState.MemoryDataRequest -> setProgressIndicator(
                memoryDataLoadState.percentProgress,
                getString(ru.profitsw2000.core.R.string.memory_data_request_status, memoryDataLoadState.percentProgress)
            )
            is MemoryDataLoadState.MemoryDataReceived -> requestNextMemoryDataPacket(memoryDataLoadState.percentProgress)
            MemoryDataLoadState.MemoryDataLoadStopRequest -> {}
            MemoryDataLoadState.MemoryDataLoadCompleted -> memoryViewModel.writeLoadedMemoryToDatabase(viewLifecycleOwner.lifecycleScope)
            MemoryDataLoadState.MemoryDataLoadInterrupted -> showSimpleMessage(
                getString(ru.profitsw2000.core.R.string.memory_data_load_completed_message_title),
                getString(ru.profitsw2000.core.R.string.memory_data_load_interrupted_message_text)
            )
            is MemoryDataLoadState.MemoryDataLoadRequestError -> showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.error_message_title),
                getString(ru.profitsw2000.core.R.string.request_sending_error_continue_message_text),
                getString(ru.profitsw2000.core.R.string.message_continue_button_text),
                getString(ru.profitsw2000.core.R.string.error_message_skip_button_text),
                ContinueMemoryLoad(memoryDataLoadState.prevMemoryDataLoadState)
            )
            is MemoryDataLoadState.MemoryDataLoadTimeoutError -> showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.error_message_title),
                getString(ru.profitsw2000.core.R.string.memory_data_timeout_error_message_text),
                getString(ru.profitsw2000.core.R.string.message_continue_button_text),
                getString(ru.profitsw2000.core.R.string.error_message_skip_button_text),
                ContinueMemoryLoad(memoryDataLoadState.prevMemoryDataLoadState)
            )
            MemoryDataLoadState.MemoryDataLoadDeviceConnectionError -> memoryLoadError(
                getString(ru.profitsw2000.core.R.string.device_connection_error_message_text) +
                        getString(ru.profitsw2000.core.R.string.memory_load_error_message_text)
            )

            MemoryDataLoadState.MemoryDataLoadClearRequest -> setProgressIndicator(
                0f,
                getString(ru.profitsw2000.core.R.string.memory_data_load_clear_request_status_text)
            )
            MemoryDataLoadState.MemoryDataLoadClearSuccess -> memoryLoadSuccess()
            MemoryDataLoadState.MemoryDataLoadSuccess -> memoryLoadSuccess()
            MemoryDataLoadState.MemoryDataLoadDatabaseWriteExecution -> setProgressIndicator(
                100f,
                getString(ru.profitsw2000.core.R.string.memory_data_load_write_to_db_status_text)
            )
            is MemoryDataLoadState.MemoryDataLoadDatabaseWriteError -> showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.error_message_title),
                getString(ru.profitsw2000.core.R.string.memory_data_load_write_to_db_error_status_text),
                getString(ru.profitsw2000.core.R.string.message_continue_button_text),
                getString(ru.profitsw2000.core.R.string.error_message_skip_button_text),
                ContinueMemoryLoad(memoryDataLoadState.prevMemoryDataLoadState)
            )
            MemoryDataLoadState.MemoryDataLoadDatabaseWriteSuccess -> {
                setProgressIndicator(
                    100f,
                    getString(ru.profitsw2000.core.R.string.memory_data_load_write_to_db_success_status_text)
                )
                memoryViewModel.memoryDataLoadClear(viewLifecycleOwner.lifecycleScope)
            }
            is MemoryDataLoadState.MemoryHistoryDataLoading -> setProgressIndicator(
                memoryDataLoadState.percentProgress,
                getString(ru.profitsw2000.core.R.string.memory_history_data_load_status, memoryDataLoadState.loadedMemory, memoryDataLoadState.memoryToLoad)
            )
            MemoryDataLoadState.MemoryServiceDataLoading -> setProgressIndicator(
                0f,
                getString(ru.profitsw2000.core.R.string.service_data_request_status_text)
            )
        }
    }

    private fun setRoundProgressBarState(isVisible: Boolean) = with(binding) {
        clearMemoryButton.isEnabled = !isVisible
        loadMemoryDataButton.isEnabled = !isVisible
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

    private fun memoryLoadError(message: String) {
        setMemoryLoadProgressState(false)
        showSimpleMessage(
            messageTitle = getString(ru.profitsw2000.core.R.string.error_message_title),
            messageText = message)
        memoryViewModel.setMemoryDataLoadToInitialState()
    }

    private fun populateMemoryInfoViews(
        currentAddress: Int,
        memoryPercentUsage: Float
    ) = with(binding) {
        setRoundProgressBarState(false)
        memoryUsageValueTextView.text = getString(ru.profitsw2000.core.R.string.memory_volume_text, currentAddress)
        freeMemoryValueTextView.text = getString(ru.profitsw2000.core.R.string.memory_volume_text, (TOTAL_MEMORY_BYTE_SIZE - currentAddress))
        memorySpaceIndicatorProgressBar.progress = currentAddress
        memorySpacePercentageTextView.text = getString(ru.profitsw2000.core.R.string.memory_percent_usage_status_text, memoryPercentUsage)
        memoryViewModel.setMemoryInfoToInitialState()
    }

    private fun memoryClearSuccess() {
        setRoundProgressBarState(false)
        showSimpleMessage(
            messageTitle = getString(ru.profitsw2000.core.R.string.operation_completed_message_title),
            messageText = getString(ru.profitsw2000.core.R.string.clear_memory_completed_successfully))
        memoryViewModel.setMemoryClearToInitialState()
    }

    private fun memoryLoadSuccess() {
        setMemoryLoadProgressState(false)
        showSimpleMessage(
            getString(ru.profitsw2000.core.R.string.memory_data_load_completed_message_title),
            getString(ru.profitsw2000.core.R.string.memory_data_load_completed_message_text)
        )
        memoryViewModel.setMemoryDataLoadToInitialState()
    }

    private fun setMemoryLoadProgressState(isVisible: Boolean) = with(binding) {
        if (isVisible) {
            mainViewsGroup.visibility = View.GONE
            memoryDataLoadGroup.visibility = View.VISIBLE
        } else {
            mainViewsGroup.visibility = View.VISIBLE
            memoryDataLoadGroup.visibility = View.GONE
        }
    }

    private fun setProgressIndicator(percentProgress: Float, statusText: String) = with(binding) {
        memoryDataLoadProgressBar.progress = percentProgress.toInt()
        memoryDataLoadPercentageProgress.text = getString(ru.profitsw2000.core.R.string.memory_percent_usage_status_text, percentProgress)
        memoryDataLoadStatusTextView.text = statusText
    }

    private fun requestFirstMemoryDataPacket(sensorsNumber: Int, memorySize: Int) {

        setProgressIndicator(
            0f,
            getString(
                ru.profitsw2000.core.R.string.service_data_receive_status_text,
                sensorsNumber,
                memorySize
            )
        )
        memoryViewModel.loadFirstMemoryDataPacket(viewLifecycleOwner.lifecycleScope)
    }

    private fun requestNextMemoryDataPacket(percentProgress: Float) {
        setProgressIndicator(
            percentProgress,
            getString(ru.profitsw2000.core.R.string.memory_data_received_status, percentProgress)
        )
        memoryViewModel.loadNextMemoryDataPacket(viewLifecycleOwner.lifecycleScope)
    }

/*    private fun showContinueOrSkipMessage(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String,
        prevState: MemoryDataLoadState
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                memoryViewModel.continueMemoryDataLoad(viewLifecycleOwner.lifecycleScope, prevState)
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                memoryLoadError(getString(ru.profitsw2000.core.R.string.memory_load_error_message_text))
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showStartMemoryLoadMessage(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String,
        clearMemory: Boolean
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                setMemoryLoadProgressState(true)
                memoryViewModel.startMemoryDataLoad(viewLifecycleOwner.lifecycleScope, clearMemory)
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showClearMemoryWarningMessage(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                confirmClearMemoryMessage(
                    getString(ru.profitsw2000.core.R.string.clear_memory_confirm_message_title),
                    getString(ru.profitsw2000.core.R.string.clear_memory_confirm_message_text),
                    getString(ru.profitsw2000.core.R.string.message_confirm_button_text),
                    getString(ru.profitsw2000.core.R.string.message_cancel_button_text)
                )
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun confirmClearMemoryMessage(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                memoryViewModel.clearMemory(viewLifecycleOwner.lifecycleScope)
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showQuitMessage(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                memoryViewModel.checkMemoryLoadAndStop(viewLifecycleOwner.lifecycleScope)
                navigator.navigateUp()
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }*/

    private fun showTwoButtonDialog(
        messageTitle: String,
        messageText: String,
        positiveButtonText: String,
        negativeButtonText: String,
        action: MemoryDataLoadAction
    ) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setPositiveButton(positiveButtonText) { _, _ ->
                positiveButtonClick(action)
            }
            .setNegativeButton(negativeButtonText) {dialog, _ ->
                negativeButtonClick(action)
                dialog.dismiss()
            }
            .create()
            .show()
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

    private fun positiveButtonClick(action: MemoryDataLoadAction) {
        when(action) {
            ClearMemory -> showTwoButtonDialog(
                getString(ru.profitsw2000.core.R.string.clear_memory_confirm_message_title),
                getString(ru.profitsw2000.core.R.string.clear_memory_confirm_message_text),
                getString(ru.profitsw2000.core.R.string.message_confirm_button_text),
                getString(ru.profitsw2000.core.R.string.message_cancel_button_text),
                ConfirmClearMemory
            )
            ConfirmClearMemory -> memoryViewModel.clearMemory(viewLifecycleOwner.lifecycleScope)
            is ContinueMemoryLoad -> memoryViewModel.continueMemoryDataLoad(
                viewLifecycleOwner.lifecycleScope,
                action.memoryDataLoadState
            )
            QuitMemoryLoad -> {
                memoryViewModel.checkMemoryLoadAndStop(viewLifecycleOwner.lifecycleScope)
                navigator.navigateUp()
            }
            is StartMemoryLoad -> {
                setMemoryLoadProgressState(true)
                memoryViewModel.startMemoryDataLoad(
                    viewLifecycleOwner.lifecycleScope,
                    action.memoryClear
                )
            }
        }
    }

    private fun negativeButtonClick(action: MemoryDataLoadAction) {
        when (action) {
            is ContinueMemoryLoad -> memoryLoadError(getString(ru.profitsw2000.core.R.string.memory_load_error_message_text))
            else -> {}
        }
    }

    override fun onStop() {
        super.onStop()
        memoryViewModel.setInitialState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}