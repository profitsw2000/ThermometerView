package ru.profitsw2000.data.model.state.sensorfilterscreen

sealed class LetterCodesLoadState {
    data object Loading: LetterCodesLoadState()
    data class Success(val letterCodesList: List<Pair<Int, Boolean>>): LetterCodesLoadState()
    data object Error: LetterCodesLoadState()
}