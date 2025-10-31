package ru.profitsw2000.data.model.state.memoryscreen

sealed class MemoryDataLoadState {
    data object MemoryDataLoadInitialState: MemoryDataLoadState()
    data object ServiceDataRequest: MemoryDataLoadState()
    data class ServiceDataReceived(val sensorsNumber: Int, val currentAddress: Int): MemoryDataLoadState()
    data class InvalidMemoryData(val prevMemoryDataLoadState: MemoryDataLoadState): MemoryDataLoadState()
    data class MemoryDataRequest(val percentProgress: Float): MemoryDataLoadState()
    data class MemoryDataReceived(val percentProgress: Float, val loadedMemory: Int, val memoryToLoad: Int): MemoryDataLoadState()
    data object MemoryDataLoadClearRequest: MemoryDataLoadState()
    data object MemoryDataLoadClearSuccess: MemoryDataLoadState()
    data object MemoryDataLoadDatabaseWriteExecution: MemoryDataLoadState()
    data class MemoryDataLoadDatabaseWriteError(val prevMemoryDataLoadState: MemoryDataLoadState): MemoryDataLoadState()
    data object MemoryDataLoadDatabaseWriteSuccess: MemoryDataLoadState()
    data object MemoryDataLoadStopRequest: MemoryDataLoadState()
    data object MemoryDataLoadInterrupted: MemoryDataLoadState()
    data object MemoryDataLoadCompleted: MemoryDataLoadState()
    data class MemoryDataLoadRequestError(val prevMemoryDataLoadState: MemoryDataLoadState): MemoryDataLoadState()
    data object MemoryDataLoadDeviceConnectionError: MemoryDataLoadState()
    data class MemoryDataLoadTimeoutError(val prevMemoryDataLoadState: MemoryDataLoadState): MemoryDataLoadState()
    data object MemoryDataLoadSuccess: MemoryDataLoadState()
}