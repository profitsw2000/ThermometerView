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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.profitsw2000.mainfragment.R
import ru.profitsw2000.mainfragment.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.connect -> {

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
            //updateTimeViewModel.disableBluetooth()
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
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}