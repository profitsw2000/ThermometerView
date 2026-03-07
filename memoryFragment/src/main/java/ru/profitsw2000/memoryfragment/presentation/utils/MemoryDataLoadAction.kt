package ru.profitsw2000.memoryfragment.presentation.utils

import ru.profitsw2000.data.model.state.memoryscreen.MemoryDataLoadState

sealed class MemoryDataLoadAction {
    data class ContinueMemoryLoad(val memoryDataLoadState: MemoryDataLoadState): MemoryDataLoadAction()
    data class StartMemoryLoad(val memoryClear: Boolean): MemoryDataLoadAction()
    data object ConfirmClearMemory: MemoryDataLoadAction()
    data object QuitMemoryLoad: MemoryDataLoadAction()
    data object ClearMemory: MemoryDataLoadAction()
}