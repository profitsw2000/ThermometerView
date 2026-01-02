package ru.profitsw2000.data.model.state

import ru.profitsw2000.data.model.SensorHistoryDataModel

sealed class SensorHistoryDataLoadState {
    data object Loading: SensorHistoryDataLoadState()
    data class Error(val errorMessage: String): SensorHistoryDataLoadState()
    data class Success(val sensorHistoryDataModelList: List<SensorHistoryDataModel>): SensorHistoryDataLoadState()
}