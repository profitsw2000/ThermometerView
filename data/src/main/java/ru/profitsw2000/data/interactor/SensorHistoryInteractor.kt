package ru.profitsw2000.data.interactor

import ru.profitsw2000.data.domain.local.SensorHistoryRepositoryLocal
import ru.profitsw2000.data.domain.remote.SensorHistoryRepositoryRemote
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryInteractor(
    val sensorHistoryRepositoryLocal: SensorHistoryRepositoryLocal,
    val sensorHistoryRepositoryRemote: SensorHistoryRepositoryRemote
) {

    suspend fun writeHistoryItem(
        sensorHistoryDataEntity: SensorHistoryDataEntity,
        isRemote: Boolean
    ) {
        if (isRemote) sensorHistoryRepositoryRemote.writeHistoryItem(sensorHistoryDataEntity = sensorHistoryDataEntity)
        else sensorHistoryRepositoryLocal.writeHistoryItem(sensorHistoryDataEntity = sensorHistoryDataEntity)
    }

}