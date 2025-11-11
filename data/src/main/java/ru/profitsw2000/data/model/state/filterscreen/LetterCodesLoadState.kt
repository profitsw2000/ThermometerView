package ru.profitsw2000.data.model.state.filterscreen

sealed class LetterCodesLoadState {
    data object Loading: LetterCodesLoadState()
    data class Success(val letterCodesList: List<Int>): LetterCodesLoadState()
    data object Error: LetterCodesLoadState()
}