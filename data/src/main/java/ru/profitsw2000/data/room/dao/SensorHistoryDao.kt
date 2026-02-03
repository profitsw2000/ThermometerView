package ru.profitsw2000.data.room.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.room.entity.SensorHistoryDataEntity
import java.util.Date

@Dao
interface SensorHistoryDao {

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE SensorHistoryDataEntity.sensorId LIKE :sensorId " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getSimpleSensorHistoryList(sensorId: Long, limit: Int, offset: Int): List<SensorHistoryDataEntity>

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    //Graph
    suspend fun getGraphFirstCurveSensorHistoryList(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when {
            filter.sensorIdList.isNotEmpty() -> getGraphFirstCurveSensorHistoryListById(filter, limit, offset)
            filter.letterCodeList.isNotEmpty() -> getGraphFirstCurveSensorHistoryListByLetter(filter, limit, offset)
            else -> arrayListOf<SensorHistoryDataEntity>()
        }
    }

    suspend fun getGraphFirstCurveSensorHistoryListById(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ) : List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getGraphFirstCurveSensorHistoryListByIdBaseTimeFrame(filter, limit, offset)
        else getGraphFirstCurveSensorHistoryListByIdCustomTimeFrame(filter, limit, offset)
    }

    suspend fun getGraphFirstCurveSensorHistoryListByLetter(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ) : List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrame(filter, limit, offset)
        else getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrame(filter, limit, offset)
    }

    suspend fun getGraphFirstCurveSensorHistoryListByIdBaseTimeFrame(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.fromDate == null || filter.toDate == null)
            getGraphFirstCurveSensorHistoryListByIdBaseTimeFrameAllDateRange(
                filter.sensorIdList[0],
                limit = limit,
                offset = offset
            )
        else getGraphFirstCurveSensorHistoryListByIdBaseTimeFrameSelectedDateRange(
            filter.sensorIdList[0],
            filter.fromDate!!,
            filter.toDate!!,
            limit = limit,
            offset = offset
        )
    }

    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrame(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.fromDate == null || filter.toDate == null)
            getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAllDateRange(
                filter.sensorIdList[0],
                filter.timeFrameMillis,
                filter.timeFrameDataObtainingMethod,
                limit = limit,
                offset = offset
            )
        else getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameSelectedDateRange(
            filter.sensorIdList[0],
            filter.timeFrameMillis,
            filter.timeFrameDataObtainingMethod,
            filter.fromDate!!,
            filter.toDate!!,
            limit = limit,
            offset = offset
        )
    }

    suspend fun getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrame(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.fromDate == null || filter.toDate == null)
            getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrameAllDateRange(
                filter.letterCodeList[0],
                limit = limit,
                offset = offset
            )
        else getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrameSelectedDateRange(
            filter.letterCodeList[0],
            filter.fromDate!!,
            filter.toDate!!,
            limit = limit,
            offset = offset
        )
    }

    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrame(
        filter: SensorHistoryGraphFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.fromDate == null || filter.toDate == null)
            getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAllDateRange(
                filter.letterCodeList[0],
                filter.timeFrameMillis,
                filter.timeFrameDataObtainingMethod,
                limit = limit,
                offset = offset
            )
        else getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameSelectedDateRange(
            filter.letterCodeList[0],
            filter.timeFrameMillis,
            filter.timeFrameDataObtainingMethod,
            filter.fromDate!!,
            filter.toDate!!,
            limit = limit,
            offset = offset
        )
    }

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getGraphFirstCurveSensorHistoryListByIdBaseTimeFrameAllDateRange(
        sensorId: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getGraphFirstCurveSensorHistoryListByIdBaseTimeFrameSelectedDateRange(
        sensorId: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAverageAllDateRange(
                sensorId,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameBeginAllDateRange(
                sensorId,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameEndAllDateRange(
                sensorId,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMaximumAllDateRange(
                sensorId,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMinimumAllDateRange(
                sensorId,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAverageAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameBeginAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameEndAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMaximumAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMinimumAllDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAverageSelectedDateRange(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameBeginSelectedDateRange(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameEndSelectedDateRange(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMaximumSelectedDateRange(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMinimumSelectedDateRange(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameAverageSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameBeginSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameEndSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMaximumSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByIdCustomTimeFrameMinimumSelectedDateRange(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrameAllDateRange(
        letterCode: Int,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterBaseTimeFrameSelectedDateRange(
        letterCode: Int,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAverageAllDateRange(
                letterCode,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameBeginAllDateRange(
                letterCode,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameEndAllDateRange(
                letterCode,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMaximumAllDateRange(
                letterCode,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMinimumAllDateRange(
                letterCode,
                timeFrameInMillis,
                limit = limit,
                offset = offset
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAverageAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameBeginAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameEndAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMaximumAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMinimumAllDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>


    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAverageSelectedDateRange(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameBeginSelectedDateRange(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameEndSelectedDateRange(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMaximumSelectedDateRange(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMinimumSelectedDateRange(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate,
                limit = limit,
                offset = offset
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameAverageSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameBeginSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameEndSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMaximumSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getGraphFirstCurveSensorHistoryListByLetterCustomTimeFrameMinimumSelectedDateRange(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    //Graph subsequent sensors
    suspend fun getGraphSubsequentCurvesSensorHistoryList(
        sensorIndex: Int,
        filter: SensorHistoryGraphFilterRepository,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity> {
        return when {
            filter.sensorIdList.isNotEmpty() -> getGraphSubsequentCurvesSensorHistoryListById(
                sensorIndex,
                filter,
                fromDate,
                toDate
            )
            filter.letterCodeList.isNotEmpty() -> getGraphSubsequentCurvesSensorHistoryListByLetter(
                sensorIndex,
                filter,
                fromDate,
                toDate
            )
            else -> arrayListOf<SensorHistoryDataEntity>()
        }
    }

    suspend fun getGraphSubsequentCurvesSensorHistoryListById(
        sensorIndex: Int,
        filter: SensorHistoryGraphFilterRepository,
        fromDate: Date,
        toDate: Date
    ) : List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getGraphSubsequentCurvesSensorHistoryListByIdBaseTimeFrame(
                filter.sensorIdList[sensorIndex],
                fromDate,
                toDate
            )
        else getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrame(
                filter.sensorIdList[sensorIndex],
                filter.timeFrameMillis,
                filter.timeFrameDataObtainingMethod,
                fromDate,
                toDate
            )
    }

    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetter(
        sensorIndex: Int,
        filter: SensorHistoryGraphFilterRepository,
        fromDate: Date,
        toDate: Date
    ) : List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getGraphSubsequentCurvesSensorHistoryListByLetterBaseTimeFrame(
                filter.letterCodeList[sensorIndex],
                fromDate,
                toDate
            )
        else getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrame(
            filter.letterCodeList[sensorIndex],
            filter.timeFrameMillis,
            filter.timeFrameDataObtainingMethod,
            fromDate,
            toDate
        )
    }

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "ORDER BY SensorHistoryDataEntity.date DESC ")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdBaseTimeFrame(
        sensorId: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrame(
        sensorId: Long,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameAverage(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameBegin(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameEnd(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameMaximum(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameMinimum(
                sensorId,
                timeFrameInMillis,
                fromDate,
                toDate
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameAverage(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameBegin(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameEnd(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameMaximum(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId " +
            "AND date BETWEEN (:fromDate) AND (:toDate) " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByIdCustomTimeFrameMinimum(
        sensorId: Long,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "ORDER BY SensorHistoryDataEntity.date DESC ")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterBaseTimeFrame(
        letterCode: Int,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrame(
        letterCode: Int,
        timeFrameInMillis: Long,
        timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity> {
        return when(timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage -> getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameAverage(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameBegin -> getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameBegin(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameEnd -> getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameEnd(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameMaximum -> getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameMaximum(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate
            )
            TimeFrameDataObtainingMethod.TimeFrameMinimum -> getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameMinimum(
                letterCode,
                timeFrameInMillis,
                fromDate,
                toDate
            )
        }
    }

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameAverage(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date ASC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameBegin(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY SensorHistoryDataEntity.date DESC)" +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameEnd(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameMaximum(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE letterCode LIKE :letterCode " +
            "AND date BETWEEN (:fromDate) AND (:toDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY SensorHistoryDataEntity.date DESC")
    suspend fun getGraphSubsequentCurvesSensorHistoryListByLetterCustomTimeFrameMinimum(
        letterCode: Int,
        timeFrameInMillis: Long,
        fromDate: Date,
        toDate: Date
    ): List<SensorHistoryDataEntity>

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    //Table functions
    suspend fun getSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.sensorIdList.isEmpty()
            && filter.localIdList.isEmpty()
            && filter.letterCodeList.isEmpty()) {
            getSensorHistoryListWithoutMainFilters(
                filter = filter,
                limit = limit,
                offset = offset
            )
        }
        else getFilteredSensorHistoryList(
            filter = filter,
            limit = limit,
            offset = offset
        )
    }

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:fromDate) AND (:toDate) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getUnfilteredSensorHistoryList(
        fromDate: Date,
        toDate: Date,
        orderIsAscending: Boolean,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT * FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN SensorHistoryDataEntity.date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN SensorHistoryDataEntity.date END DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun getFilteredByMainFieldsSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate) " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsAverageTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsMaxTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsMinTemperatureGroupedByDateSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE (sensorId IN (:sensorIdList) " +
            "OR localId IN (:localIdList) " +
            "OR letterCode IN (:letterCodeList)) " +
            "AND date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis)" +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY " +
                        "CASE WHEN (:isFirstValue) THEN SensorHistoryDataEntity.date END DESC, " +
                        "CASE WHEN NOT (:isFirstValue) THEN SensorHistoryDataEntity.date END ASC " +
            ")" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
        sensorIdList: List<Long>,
        localIdList: List<Int>,
        letterCodeList: List<Int>,
        timeFrameInMillis: Long,
        isFirstValue: Boolean,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT AVG(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate) " +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getAverageTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MAX(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getMaxTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT MIN(temperature) AS temperature, " +
            "MIN(id) AS id, " +
            "sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getMinTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    @Query("SELECT DISTINCT FIRST_VALUE(temperature) OVER w AS temperature, " +
            "MIN(date) OVER w AS date, id, sensorId, localId, letterCode " +
            "FROM SensorHistoryDataEntity " +
            "WHERE date BETWEEN (:startDate) AND (:endDate)" +
            "GROUP BY sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "WINDOW w AS (" +
            "   PARTITION BY" +
            "       sensorId, (date + (3*60*60*1000))/(:timeFrameInMillis) " +
            "       ORDER BY " +
            "           CASE WHEN (:isFirstValue) THEN SensorHistoryDataEntity.date END DESC, " +
            "           CASE WHEN NOT (:isFirstValue) THEN SensorHistoryDataEntity.date END ASC " +
            ")" +
            "ORDER BY " +
            "CASE WHEN (:orderIsAscending) THEN date END ASC, " +
            "CASE WHEN NOT (:orderIsAscending) THEN date END DESC " +
            "LIMIT :limit OFFSET :offset ")
    suspend fun getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
        timeFrameInMillis: Long,
        isFirstValue: Boolean,
        orderIsAscending: Boolean,
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity>

    private suspend fun getSensorHistoryListWithoutMainFilters(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getUnfilteredSensorHistoryList(
                fromDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                toDate = filter.toDate ?: Date(Long.MAX_VALUE),
                orderIsAscending = filter.isAscendingOrder,
                limit = limit,
                offset = offset
            )
        else
            getTimeFrameTemperatureValueWithoutMainFiltersSensorHistoryList(
                filter = filter,
                limit = limit,
                offset = offset
            )
    }

    private suspend fun getFilteredSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return if (filter.timeFrameMillis == TEN_MINUTES_FRAME_MILLIS)
            getFilteredByMainFieldsSensorHistoryList(
                sensorIdList = filter.sensorIdList,
                localIdList = filter.localIdList,
                letterCodeList = filter.letterCodeList,
                orderIsAscending = filter.isAscendingOrder,
                startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                limit = limit,
                offset = offset
            )
        else
            getFilteredTimeFrameTemperatureValueSensorHistoryList(
                filter = filter,
                limit = limit,
                offset = offset
            )
    }

    private suspend fun getTimeFrameTemperatureValueWithoutMainFiltersSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(filter.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage ->
                getAverageTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMaximum ->
                getMaxTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMinimum ->
                getMinTemperatureGroupedByDateWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameBegin ->
                getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = true,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameEnd ->
                getTimeFrameBoundaryTemperatureValueWithoutMainFiltersSensorHistoryList(
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = false,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
        }
    }

    private suspend fun getFilteredTimeFrameTemperatureValueSensorHistoryList(
        filter: SensorHistoryTableFilterRepository,
        limit: Int,
        offset: Int
    ): List<SensorHistoryDataEntity> {
        return when(filter.timeFrameDataObtainingMethod) {
            TimeFrameDataObtainingMethod.TimeFrameAverage ->
                getFilteredByMainFieldsAverageTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMaximum ->
                getFilteredByMainFieldsMaxTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameMinimum ->
                getFilteredByMainFieldsMinTemperatureGroupedByDateSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameBegin ->
                getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = true,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
            TimeFrameDataObtainingMethod.TimeFrameEnd ->
                getFilteredByMainFieldsTimeFrameBoundaryTemperatureValueSensorHistoryList(
                    sensorIdList = filter.sensorIdList,
                    localIdList = filter.localIdList,
                    letterCodeList = filter.letterCodeList,
                    timeFrameInMillis = filter.timeFrameMillis,
                    isFirstValue = false,
                    orderIsAscending = filter.isAscendingOrder,
                    startDate = filter.fromDate ?: Date(Long.MIN_VALUE),
                    endDate = filter.toDate ?: Date(Long.MAX_VALUE),
                    limit = limit,
                    offset = offset
                )
        }
    }

    @Query("SELECT DISTINCT sensorId FROM SensorHistoryDataEntity")
    suspend fun getAllSensorsIdList(): List<Long>

    @Query("SELECT DISTINCT localId FROM SensorHistoryDataEntity")
    suspend fun getAllSensorsLocalIdList(): List<Int>

    @Query("SELECT DISTINCT letterCode FROM SensorHistoryDataEntity")
    suspend fun getAllLetterCodesList(): List<Int>

    @Query("SELECT COUNT(*) FROM SensorHistoryDataEntity")
    suspend fun getSensorHistoryDataEntityCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sensorHistoryDataEntity: SensorHistoryDataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(sensorHistoryDataEntityList: List<SensorHistoryDataEntity>)
}