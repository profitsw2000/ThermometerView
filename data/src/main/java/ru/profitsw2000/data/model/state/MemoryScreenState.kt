package ru.profitsw2000.data.model.state

import ru.profitsw2000.data.model.MemoryInfoModel

sealed class MemoryScreenState {
    data object Blank: MemoryScreenState()
    data object MemoryInfoLoad: MemoryScreenState()
    data class MemoryInfoSuccess(val memoryInfoModel: MemoryInfoModel): MemoryScreenState()
    data object MemoryClearExecution: MemoryScreenState()
    data object MemoryClearSuccess: MemoryScreenState()
    data class ServiceDataRequest(val message: String): MemoryScreenState()
    data class ServiceDataAnswer(val message: String): MemoryScreenState()
    data class MemoryDataRequest(val message: String, val percentProgress: Int): MemoryScreenState()
    data class MemoryDataAnswer(val message: String, val percentProgress: Int): MemoryScreenState()
    data object MemoryDataSuccess: MemoryScreenState()
    data object TimeoutError: MemoryScreenState()
    data class Error(val message: String): MemoryScreenState()
}