package ru.profitsw2000.data.mappers

import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity

class SensorHistoryMapper {

    fun map(sensorHistoryDataModel: SensorHistoryDataModel): SensorHistoryDataEntity {
        return SensorHistoryDataEntity(
            id = 0,
            localId = sensorHistoryDataModel.localId,
            sensorId = sensorHistoryDataModel.sensorId,
            letterCode = sensorHistoryDataModel.localId,
            date = sensorHistoryDataModel.date,
            temperature = sensorHistoryDataModel.temperature,
        )
    }

    fun map(sensorHistoryDataEntity: SensorHistoryDataEntity): SensorHistoryDataModel {
        return SensorHistoryDataModel(
            localId = sensorHistoryDataEntity.localId,
            sensorId = sensorHistoryDataEntity.sensorId,
            letterCode = sensorHistoryDataEntity.localId,
            date = sensorHistoryDataEntity.date,
            temperature = sensorHistoryDataEntity.temperature,
        )
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("mapEntityList")
    fun map(sensorHistoryDataModelList: List<SensorHistoryDataModel>): List<SensorHistoryDataEntity> {
        return sensorHistoryDataModelList.map { map(it) }
    }

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("mapModelList")
    fun map(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>): List<SensorHistoryDataModel> {
        return sensorHistoryDataEntityList.map { map(it) }
    }

}