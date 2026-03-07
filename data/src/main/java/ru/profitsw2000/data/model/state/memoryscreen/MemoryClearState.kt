package ru.profitsw2000.data.model.state.memoryscreen

import ru.profitsw2000.data.model.MemoryInfoModel

sealed class MemoryClearState {
    data object MemoryClearInitialState: MemoryClearState()
    data object MemoryClearExecution: MemoryClearState()
    data object MemoryClearSuccess: MemoryClearState()
    data object MemoryClearError: MemoryClearState()
    data object MemoryClearSendRequestError: MemoryClearState()
    data object MemoryClearDeviceConnectionError: MemoryClearState()
    data object MemoryClearTimeoutError: MemoryClearState()
}