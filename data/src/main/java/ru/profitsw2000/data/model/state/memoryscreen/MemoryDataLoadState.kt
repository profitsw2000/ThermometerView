package ru.profitsw2000.data.model.state.memoryscreen

import ru.profitsw2000.data.model.state.MemoryScreenState

sealed class MemoryDataLoadState {
    data object MemoryDataLoadInitialState: MemoryDataLoadState()
    data class ServiceDataRequest(val message: String): MemoryDataLoadState()
    data class ServiceDataReceived(val message: String): MemoryDataLoadState()
    data class MemoryDataRequest(val message: String, val percentProgress: Int): MemoryDataLoadState()
    data class MemoryDataReceived(val message: String, val percentProgress: Int): MemoryDataLoadState()
    data class MemoryDataLoadStopRequest(val message: String): MemoryDataLoadState()
    data object MemoryDataLoadStopReceived: MemoryDataLoadState()
    data object MemoryDataLoadRequestError: MemoryDataLoadState()
    data object MemoryDataLoadDeviceConnectionError: MemoryDataLoadState()
    data object MemoryDataLoadTimeoutError: MemoryDataLoadState()
}