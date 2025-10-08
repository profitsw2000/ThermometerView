package ru.profitsw2000.data.model.state.memoryscreen

sealed class MemoryDataLoadState {
    data object MemoryDataLoadInitialState: MemoryDataLoadState()
    data object ServiceDataRequest: MemoryDataLoadState()
    data class ServiceDataReceived(val sensorsNumber: Int, val currentAddress: Int): MemoryDataLoadState()
    data object InvalidMemoryServiceDataError: MemoryDataLoadState()
    data class MemoryDataRequest(val percentProgress: Float): MemoryDataLoadState()
    data class MemoryDataReceived(val percentProgress: Float): MemoryDataLoadState()
    data object InvalidMemoryDataError: MemoryDataLoadState()
    data object MemoryDataLoadStopRequest: MemoryDataLoadState()
    data object MemoryDataLoadStopReceived: MemoryDataLoadState()
    data object MemoryDataLoadRequestError: MemoryDataLoadState()
    data object MemoryDataLoadDeviceConnectionError: MemoryDataLoadState()
    data object MemoryDataLoadTimeoutError: MemoryDataLoadState()
}