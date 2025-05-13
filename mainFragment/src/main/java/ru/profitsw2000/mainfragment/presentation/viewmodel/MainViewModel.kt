package ru.profitsw2000.mainfragment.presentation.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import ru.profitsw2000.data.domain.BluetoothRepository

class MainViewModel(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel(), DefaultLifecycleObserver {



    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }
}