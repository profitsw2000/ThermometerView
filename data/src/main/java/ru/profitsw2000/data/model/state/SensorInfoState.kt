package ru.profitsw2000.data.model.state

import ru.profitsw2000.data.model.SensorModel

sealed class SensorInfoState {
    data object Loading: SensorInfoState()
    data object Error: SensorInfoState()
    data class Success(val sensorModel: SensorModel): SensorInfoState()
}