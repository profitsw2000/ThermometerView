package ru.profitsw2000.mainfragment.presentation.view

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.data.model.BluetoothConnectionStatus
import ru.profitsw2000.mainfragment.R
import ru.profitsw2000.mainfragment.databinding.FragmentMainBinding
import ru.profitsw2000.mainfragment.presentation.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModel()
    private var bluetoothIsEnabled = false
    private val requestCodeForEnable = 1
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            switchBluetooth()
        } else {
            showRationaleDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.initBluetooth(bluetoothPermissionIsGranted())
        lifecycle.addObserver(mainViewModel)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.bind(inflater.inflate(R.layout.fragment_main, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.connect -> {
                mainViewModel.deviceConnection()
                true
            }
            R.id.bluetooth -> {
                bluetoothOperation()
                true
            }
            else -> true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (bluetoothIsEnabled)
            menu.findItem(R.id.bluetooth).setIcon(ru.profitsw2000.core.R.drawable.bluetooth_on_icon)
        else
            menu.findItem(R.id.bluetooth).setIcon(ru.profitsw2000.core.R.drawable.bluetooth_off_icon)

        menu.findItem(R.id.connect).setIcon(getResourceId(mainViewModel.bluetoothConnectionStatus.value))
    }

    private fun observeData() {
        observeBluetoothStateData()
        observeBluetoothConnectionStatus()
    }

    private fun observeBluetoothStateData() {
        val observer = Observer<Boolean> { renderBluetoothStateData(it) }
        mainViewModel.bluetoothIsEnabledData.observe(viewLifecycleOwner, observer)
    }

    private fun renderBluetoothStateData(isEnabled: Boolean) {
        bluetoothIsEnabled = isEnabled
        requireActivity().invalidateOptionsMenu()
    }

    private fun observeBluetoothConnectionStatus() {
        val observer = Observer<BluetoothConnectionStatus> { renderBluetoothConnectionStatusData(it) }
        mainViewModel.bluetoothConnectionStatus.observe(viewLifecycleOwner, observer)
    }

    private fun renderBluetoothConnectionStatusData(bluetoothConnectionStatus: BluetoothConnectionStatus) {
        when(bluetoothConnectionStatus) {
            BluetoothConnectionStatus.Connected -> setButtonsState(true)
            BluetoothConnectionStatus.Connecting -> setButtonsState(false)
            BluetoothConnectionStatus.DeviceSelection -> startBottomSheetDialog()
            BluetoothConnectionStatus.Disconnected -> setButtonsState(false)
            BluetoothConnectionStatus.Failed -> setButtonsState(false)
        }
        requireActivity().invalidateOptionsMenu()
    }

    private fun bluetoothOperation() {
        if (VERSION.SDK_INT > VERSION_CODES.R) {
            getBluetoothPermission()
        } else {
            switchBluetooth()
        }
    }

    private fun getBluetoothPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED ->
                switchBluetooth()

            //////////////////////////////////////////////////////////////////

            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT) -> showRationaleDialog()

            //////////////////////////////////////////////////////////////////

            else -> requestPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    @SuppressLint("MissingPermission")
    private fun switchBluetooth() {
        if (bluetoothIsEnabled) {
            mainViewModel.disableBluetooth()
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, requestCodeForEnable)
        }
    }

    private fun showRationaleDialog() {
        showSimpleDialog(messageTitle = getString(ru.profitsw2000.core.R.string.bluetooth_permission_rationale_dialog_title),
            messageText = getString(ru.profitsw2000.core.R.string.bluetooth_permission_rationale_dialog_text),
            buttonText = getString(ru.profitsw2000.core.R.string.ok_dialog_button_text))
    }

    private fun showBluetoothEnablingDialog() {
        showSimpleDialog(messageTitle = getString(ru.profitsw2000.core.R.string.bluetooth_on_warning_dialog_title),
            messageText = getString(ru.profitsw2000.core.R.string.bluetooth_on_warning_dialog_text),
            buttonText = getString(ru.profitsw2000.core.R.string.ok_dialog_button_text))
    }

    private fun showSimpleDialog(messageTitle: String,
                                 messageText: String,
                                 buttonText: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(messageTitle)
            .setMessage(messageText)
            .setNeutralButton(buttonText) { dialog, _ -> dialog.dismiss()}
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCodeForEnable) {
            if (resultCode == Activity.RESULT_CANCELED) {
                showBluetoothEnablingDialog()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun bluetoothPermissionIsGranted(): Boolean {
        return if (VERSION.SDK_INT > VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun startBottomSheetDialog() {
        //val bluetoothPairedDevicesListFragment = BluetoothPairedDevicesListFragment()

        setButtonsState(false)
        //bluetoothPairedDevicesListFragment.show(parentFragmentManager, "devices list")
    }

    private fun setButtonsState(isEnabled: Boolean) = with(binding) {
        updateTimeButton.isEnabled = isEnabled
        changeMemoryButton.isEnabled = isEnabled
    }

    private fun getResourceId(bluetoothConnectionStatus: BluetoothConnectionStatus?): Int {
        return when(bluetoothConnectionStatus){
            BluetoothConnectionStatus.Connected -> ru.profitsw2000.core.R.drawable.icon_connected
            BluetoothConnectionStatus.Connecting -> ru.profitsw2000.core.R.drawable.icon_connecting
            BluetoothConnectionStatus.DeviceSelection -> ru.profitsw2000.core.R.drawable.icon_disconnected
            BluetoothConnectionStatus.Disconnected -> ru.profitsw2000.core.R.drawable.icon_disconnected
            BluetoothConnectionStatus.Failed -> ru.profitsw2000.core.R.drawable.icon_disconnected
            else -> ru.profitsw2000.core.R.drawable.icon_disconnected
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}