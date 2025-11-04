package ru.profitsw2000.data.model.state.memoryscreen

import ru.profitsw2000.data.model.MemoryInfoModel

sealed class MemoryInfoState {
    data object MemoryInfoInitialState: MemoryInfoState()
    data object MemoryInfoLoad: MemoryInfoState()
    data class MemoryInfoSuccess(val memoryInfoModel: MemoryInfoModel): MemoryInfoState()
    data object MemoryInfoSendRequestError: MemoryInfoState()
    data object MemoryInfoDeviceConnectionError: MemoryInfoState()
    data object MemoryInfoTimeoutError: MemoryInfoState()
}