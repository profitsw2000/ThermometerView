package ru.profitsw2000.data.model.state.filterscreen

sealed class LocalIdsLoadState {
    data object Loading: LocalIdsLoadState()
    data class Success(val localIdsList: List<Int>): LocalIdsLoadState()
    data object Error: LocalIdsLoadState()
}