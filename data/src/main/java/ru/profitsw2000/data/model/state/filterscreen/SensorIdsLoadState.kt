package ru.profitsw2000.data.model.state.filterscreen

sealed class SensorIdsLoadState {
    data object Loading: SensorIdsLoadState()
    data class Success(val sensorIdsList: List<Long>): SensorIdsLoadState()
    data object Error: SensorIdsLoadState()
}