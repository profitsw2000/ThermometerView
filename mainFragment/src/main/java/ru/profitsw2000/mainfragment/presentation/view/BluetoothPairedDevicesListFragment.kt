package ru.profitsw2000.mainfragment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.profitsw2000.mainfragment.R

class BluetoothPairedDevicesListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_paired_devices_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = BluetoothPairedDevicesListFragment()
    }
}