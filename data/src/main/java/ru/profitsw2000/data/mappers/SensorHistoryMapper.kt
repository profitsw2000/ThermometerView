package ru.profitsw2000.data.mappers

import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_FACTOR
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.SensorHistoryTimeFrameDataModel
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

class SensorHistoryMapper {

    fun map(sensorHistoryDataModel: SensorHistoryDataModel): SensorHistoryDataEntity {
        return SensorHistoryDataEntity(
            id = 0,
            localId = sensorHistoryDataModel.localId,
            sensorId = sensorHistoryDataModel.sensorId,
            letterCode = sensorHistoryDataModel.letterCode,
            date = sensorHistoryDataModel.date,
            temperature = sensorHistoryDataModel.temperature,
        )
    }

    fun map(sensorHistoryDataEntity: SensorHistoryDataEntity): SensorHistoryDataModel {
        return SensorHistoryDataModel(
            localId = sensorHistoryDataEntity.localId,
            sensorId = sensorHistoryDataEntity.sensorId,
            letterCode = sensorHistoryDataEntity.letterCode,
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

    fun filteredMap(
        sensorHistoryDataEntityList: List<SensorHistoryDataEntity>,
        timeFrameFactor: Int,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        isAscendingOrder: Boolean
    ): List<SensorHistoryDataModel> {
        return if (timeFrameFactor == TEN_MINUTES_FRAME_FACTOR)
            map(sensorHistoryDataEntityList = sensorHistoryDataEntityList)
        else
            getFilteredSensorHistoryList(
                sensorHistoryDataEntityList = sensorHistoryDataEntityList,
                timeFrameFactor = timeFrameFactor,
                timeFrameDataObtainingMethod = timeFrameDataObtainingMethod,
                isAscendingOrder = isAscendingOrder
            )
    }

    private fun getFilteredSensorHistoryList(
        sensorHistoryDataEntityList: List<SensorHistoryDataEntity>,
        timeFrameFactor: Int,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        isAscendingOrder: Boolean
    ): List<SensorHistoryDataModel> {
        val sensorHistoryTimeFrameDataModelList = mutableListOf<SensorHistoryTimeFrameDataModel>()
        val sensorHistoryDataModelMutableList = mutableListOf<SensorHistoryDataModel>()

        sensorHistoryDataEntityList.forEach { item ->
            val sensorIndexInList = getIndexOfElementInSensorHistoryTimeFrameDataModelListBySerialNumber(
                sensorHistoryTimeFrameDataModelList,
                item.sensorId
            )

            if (sensorIndexInList != -1) {
                val isInRange = isDateInRange(
                    date = item.date,
                    timeFrameEnd = sensorHistoryTimeFrameDataModelList[sensorIndexInList].timeFrameEndDate,
                    timeFrameStart = sensorHistoryTimeFrameDataModelList[sensorIndexInList].timeFrameStartDate,
                    isAscendingDate = isAscendingOrder
                )
                if (isInRange)
                    sensorHistoryTimeFrameDataModelList[sensorIndexInList].temperatureMutableList.add(item.temperature)
                else
                    calculateTemperatureAndUpdateFrame()
            }
        }
    }

    private fun getIndexOfElementInSensorHistoryTimeFrameDataModelListBySerialNumber(
        sensorHistoryTimeFrameDataModelList: List<SensorHistoryTimeFrameDataModel>,
        sensorId: Long
    ): Int {
        sensorHistoryTimeFrameDataModelList.forEachIndexed { index, item ->
            if (item.sensorHistoryDataModel.sensorId == sensorId) return index
        }
        return -1
    }

    private fun isDateInRange(
        date: Date,
        timeFrameEnd: Date,
        timeFrameStart: Date,
        isAscendingDate: Boolean
    ): Boolean {
        return if (isAscendingDate)
            (date.after(timeFrameStart) || date == timeFrameStart) && date.before(timeFrameEnd)
        else
            (date.before(timeFrameStart) || date == timeFrameStart) && date.after(timeFrameEnd)
    }

    private fun getTimeFrameEndDate(
        timeFrameStart: Date,
        timeFrameFactor: Int,
        isAscendingDate: Boolean
    ): Date {
        val timeFrameInMillis = if (isAscendingDate) (timeFrameFactor*10*60*1000L)
        else -(timeFrameFactor*10*60*1000L)

        return Date(timeFrameStart.time + timeFrameInMillis)
    }

    private fun calculateTemperatureAndUpdateFrame() {}
}