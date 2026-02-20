package ru.profitsw2000.data.model.state.sensorfilterscreen

sealed class SensorIdsLoadState {
    data object Loading: SensorIdsLoadState()
    data class Success(val sensorIdsList: List<Pair<Int, Boolean>>): SensorIdsLoadState()
    data object Error: SensorIdsLoadState()
}