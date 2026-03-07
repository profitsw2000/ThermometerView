package ru.profitsw2000.graphtab.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.definition.indexKey
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.TEN_MINUTES_FRAME_MILLIS
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.SensorHistoryDataLoadState
import ru.profitsw2000.data.model.state.sensorfilterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.sensorfilterscreen.SensorIdsLoadState
import java.util.Date
import kotlin.getValue

const val SENSOR_HISTORY_DATA_LOAD_SIZE = 48

class GraphViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor,
    private val sensorHistoryMapper: SensorHistoryMapper,
    private val sensorHistoryGraphFilterRepository: SensorHistoryGraphFilterRepository
) : ViewModel() {
    //coroutine
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var lifecycleScope: CoroutineScope
    val selectedSensorIdsMutableList = mutableListOf<Long>()
    val selectedLetterCodesMutableList = mutableListOf<Int>()
    private var fromDate: Date? = null
    private var toDate: Date? = null
    var offset = 0
    var totalQueryCount: Int = 0

    //LiveData
    private val _sensorHistoryListLiveData: MutableLiveData<SensorHistoryDataLoadState> =
        MutableLiveData<SensorHistoryDataLoadState>()
    val sensorHistoryListLiveData: LiveData<SensorHistoryDataLoadState> by this::_sensorHistoryListLiveData

    private val _sensorIdsListLiveData: MutableLiveData<SensorIdsLoadState> =
        MutableLiveData<SensorIdsLoadState>()
    val sensorIdsListLiveData: LiveData<SensorIdsLoadState> by this::_sensorIdsListLiveData

    private val _letterCodesListLiveData: MutableLiveData<LetterCodesLoadState> =
        MutableLiveData<LetterCodesLoadState>()
    val letterCodesListLiveData: LiveData<LetterCodesLoadState> by this::_letterCodesListLiveData

    init {
        viewModelScope.launch {
            val sensorIdsList = getSensorIdsList()
            if (!sensorIdsList.isNullOrEmpty())
                sensorHistoryGraphFilterRepository.sensorIdList = listOf(sensorIdsList[0])
            loadInitData()
        }
    }

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    private suspend fun loadInitData() {
        offset = 0
        totalQueryCount = getFirstSensorHistoryListSize()
        val graphData = getFilteredSensorsHistoryLists()
        if (graphData != null && totalQueryCount != -1)
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
        else
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error("Database error.")
    }

    fun loadData(newItemsNumber: Int) {
        offset += newItemsNumber
        offset = when {
            (offset > totalQueryCount - SENSOR_HISTORY_DATA_LOAD_SIZE) && (totalQueryCount > SENSOR_HISTORY_DATA_LOAD_SIZE) -> totalQueryCount - SENSOR_HISTORY_DATA_LOAD_SIZE
            (offset > totalQueryCount - SENSOR_HISTORY_DATA_LOAD_SIZE) && (totalQueryCount <= SENSOR_HISTORY_DATA_LOAD_SIZE) -> 0
            offset < 0 -> 0
            else -> offset
        }

        viewModelScope.launch {
            val graphData = getFilteredSensorsHistoryLists()
            if (graphData != null)
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
            else
                _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error("Database error.")
        }
    }

    fun loadSensorsIdsAndLetters() {
        _sensorIdsListLiveData.value = SensorIdsLoadState.Loading
        _letterCodesListLiveData.value = LetterCodesLoadState.Loading
        loadSensorIds()
        loadLetterCodes()
    }

    fun changeSelectedSensorIdsList(sensorId: Long, isRemove: Boolean) {
        selectedLetterCodesMutableList.clear()
        if (isRemove) selectedSensorIdsMutableList.remove(sensorId)
        else
            if (!selectedSensorIdsMutableList.contains(sensorId)) selectedSensorIdsMutableList.add(sensorId)
    }

    fun changeSelectedLetterCodesList(letterCode: Int, isRemove: Boolean) {
        selectedSensorIdsMutableList.clear()
        if (isRemove) selectedLetterCodesMutableList.remove(letterCode)
        else
            if (!selectedLetterCodesMutableList.contains(letterCode)) selectedLetterCodesMutableList.add(letterCode)
    }

    fun setSensorIdsAndLettersFilters() {
        sensorHistoryGraphFilterRepository.sensorIdList = selectedSensorIdsMutableList
        sensorHistoryGraphFilterRepository.letterCodeList = selectedLetterCodesMutableList
        viewModelScope.launch {
            loadInitData()
        }
    }

    fun getTimeFrameFilter(): Long {
        return sensorHistoryGraphFilterRepository.timeFrameMillis
    }

    fun setTimeFrameFilter(timeFrameMillis: Long) {
        if (timeFrameMillis != sensorHistoryGraphFilterRepository.timeFrameMillis) {
            sensorHistoryGraphFilterRepository.timeFrameMillis = timeFrameMillis
            viewModelScope.launch {
                loadInitData()
            }
        }
    }

    fun getTimeFrameDataObtainingMethodFilter(): TimeFrameDataObtainingMethod {
        return sensorHistoryGraphFilterRepository.timeFrameDataObtainingMethod
    }

    fun setTimeFrameDataObtainingMethodFilter(timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod) {
        if (timeFrameDataObtainingMethod != sensorHistoryGraphFilterRepository.timeFrameDataObtainingMethod) {
            sensorHistoryGraphFilterRepository.timeFrameDataObtainingMethod = timeFrameDataObtainingMethod
            viewModelScope.launch {
                loadInitData()
            }
        }
    }

    fun getGraphFilterDatePeriod(): Pair<Date?, Date?> {
        return Pair(
            sensorHistoryGraphFilterRepository.fromDate,
            sensorHistoryGraphFilterRepository.toDate
        )
    }

    fun setGraphFilterDatePeriod() {
        if (this.fromDate != sensorHistoryGraphFilterRepository.fromDate ||
            this.toDate != sensorHistoryGraphFilterRepository.toDate) {
            sensorHistoryGraphFilterRepository.fromDate = this.fromDate
            sensorHistoryGraphFilterRepository.toDate = this.toDate
            viewModelScope.launch {
                loadInitData()
            }
        }
    }

    fun changeDatePeriod(fromDate: Date?, toDate: Date?) {
        this.fromDate = fromDate
        this.toDate = toDate
    }

    private fun loadSensorIds() {
        viewModelScope.launch {
            val sensorIds = getSensorIdsList()

            if (sensorIds != null)
                _sensorIdsListLiveData.value = SensorIdsLoadState.Success(
                    getSensorIdsListPair(sensorIds)
                )
            else
                _sensorIdsListLiveData.value = SensorIdsLoadState.Error
        }
    }

    private fun loadLetterCodes() {
        viewModelScope.launch {
            val letterCodes = getLetterCodesList()

            if (letterCodes != null)
                _letterCodesListLiveData.value = LetterCodesLoadState.Success(
                    getLetterCodesListPair(letterCodes)
                )
            else
                _letterCodesListLiveData.value = LetterCodesLoadState.Error
        }
    }

    private fun getSensorIdsListPair(
        sensorIdsList: List<Long>
    ): List<Pair<Long, Boolean>> {
        val sensorIdsPairList = mutableListOf<Pair<Long, Boolean>>()

        sensorIdsList.forEach { sensorId ->
            sensorIdsPairList.add(
                Pair(sensorId, sensorHistoryGraphFilterRepository.sensorIdList.contains(sensorId))
            )
        }
        return sensorIdsPairList
    }

    private fun getLetterCodesListPair(
        letterCodesList: List<Int>
    ): List<Pair<Int, Boolean>> {
        val letterCodesPairList = mutableListOf<Pair<Int, Boolean>>()

        letterCodesList.forEach { letterCode ->
            letterCodesPairList.add(
                Pair(letterCode, sensorHistoryGraphFilterRepository.letterCodeList.contains(letterCode))
            )
        }
        return letterCodesPairList
    }

    private suspend fun getFilteredSensorsHistoryLists(): List<List<SensorHistoryDataModel>>? {
        val firstSensorHistoryList = getFirstSensorHistoryList()
        val result = if (!firstSensorHistoryList.isNullOrEmpty()) {
            val endDate = firstSensorHistoryList.first().date
            val beginDate = firstSensorHistoryList.last().date
            val subsequentSensorsHistoryLists = getSubsequentHistoryLists(beginDate, endDate)

            if (subsequentSensorsHistoryLists.isNullOrEmpty()) listOf(firstSensorHistoryList)
            else listOf(firstSensorHistoryList) + subsequentSensorsHistoryLists
        } else null

        return result
    }

    private suspend fun getFirstSensorHistoryList(): List<SensorHistoryDataModel>? = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                try {
                    val sensorHistoryDataList = sensorHistoryInteractor.getGraphFirstCurveSensorHistoryList(
                        limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                        offset = offset,
                        false
                    )
                    sensorHistoryMapper.map(sensorHistoryDataList)
                } catch (exception: Exception) {
                    null
                }
            }.await()
        }
        /*val deferred: Deferred<List<SensorHistoryDataModel>?> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getGraphFirstCurveSensorHistoryList(
                    limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                    offset = offset,
                    false
                )
                sensorHistoryMapper.map(sensorHistoryDataList)
            } catch (exception: Exception) {
                null
            }
        }
        return@withContext deferred.await()*/
    }

    private suspend fun getFirstSensorHistoryListSize(): Int = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                try {
                    sensorHistoryInteractor.getGraphSensorHistoryListCount(false)
                } catch (exception: Exception) {
                    -1
                }
            }.await()
        }
    }

    private suspend fun getSubsequentHistoryLists(
        fromDate: Date,
        toDate: Date
    ): List<List<SensorHistoryDataModel>>? = withContext(Dispatchers.IO) {
        runCatching {
            coroutineScope {
                val sensorsNumber = if (sensorHistoryGraphFilterRepository.sensorIdList.isNotEmpty())
                    sensorHistoryGraphFilterRepository.sensorIdList.size
                else sensorHistoryGraphFilterRepository.letterCodeList.size

                (1 until sensorsNumber).map { index ->
                    async {
                        val sensorHistoryDataList = sensorHistoryInteractor.getGraphSubsequentCurvesSensorHistoryList(
                            sensorIndex = index,
                            fromDate = fromDate,
                            toDate = toDate,
                            isRemote = false
                        )
                        sensorHistoryMapper.map(sensorHistoryDataList)
                    }
                }.awaitAll()
            }
        }.getOrNull()
    }

    private suspend fun getSensorIdsList(): List<Long>? = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                try {
                    sensorHistoryInteractor.getAllSensorIds(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }.await()
        }
    }

    private suspend fun getLetterCodesList(): List<Int>? = withContext(Dispatchers.IO) {
        coroutineScope {
            async {
                try {
                    sensorHistoryInteractor.getAllLetterCodes(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }.await()
        }
    }

    /*
    Чтобы выполнить оба запроса (получение данных и подсчет общего количества) максимально быстро и эффективно, лучше всего запустить их параллельно с помощью корутин.
Вот как это реализовать в вашем репозитории или ViewModel:
1. Подготовка DAO
Убедитесь, что у вас есть оба метода. Обратите внимание: в count запросе нет ORDER BY, LIMIT и OFFSET.
kotlin
@Dao
interface SensorDao {
    // Основной запрос с лимитами
    @Query("SELECT MAX(temperature) AS temperature, MIN(id) AS id, sensorId, localId, letterCode, MIN(date) AS date " +
            "FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId AND date BETWEEN :fromDate AND :toDate " +
            "GROUP BY sensorId, (date + 10800000) / :timeFrameInMillis " +
            "ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getData(sensorId: String, fromDate: Long, toDate: Long, timeFrameInMillis: Long, limit: Int, offset: Int): List<SensorHistoryDataEntity>

    // Запрос на общее количество
    @Query("SELECT COUNT(*) FROM (SELECT 1 FROM SensorHistoryDataEntity " +
            "WHERE sensorId LIKE :sensorId AND date BETWEEN :fromDate AND :toDate " +
            "GROUP BY sensorId, (date + 10800000) / :timeFrameInMillis)")
    suspend fun getTotalCount(sensorId: String, fromDate: Long, toDate: Long, timeFrameInMillis: Long): Int
}
Используйте код с осторожностью.

2. Параллельный вызов в коде
Используйте async, чтобы не ждать выполнения запросов по очереди. Это сэкономит время, так как база данных сможет начать подготовку второго запроса, пока обрабатывается первый.
kotlin
suspend fun getSensorDataWithTotal(
    sensorId: String,
    fromDate: Long,
    toDate: Long,
    timeFrame: Long,
    limit: Int,
    offset: Int
): Pair<List<SensorHistoryDataEntity>, Int> = coroutineScope {

    // Запускаем оба запроса одновременно
    val itemsDeferred = async { dao.getData(sensorId, fromDate, toDate, timeFrame, limit, offset) }
    val countDeferred = async { dao.getTotalCount(sensorId, fromDate, toDate, timeFrame) }

    // Ждем завершения обоих и возвращаем результат
    val items = itemsDeferred.await()
    val totalCount = countDeferred.await()

    return@coroutineScope Pair(items, totalCount)
}
Используйте код с осторожностью.

Почему это круто:
Скорость: Если getData занимает 50мс и getTotalCount 40мс, то благодаря async вы получите результат примерно через 50-60мс, а не через 90мс.
Чистота: Вы возвращаете Pair (или создайте свой data class Result), который содержит и данные для текущей страницы, и общее число строк для отрисовки пагинации в UI.
Важный совет по UI
Если вы планируете отображать это в списке с бесконечной прокруткой, рассмотрите возможность использования Paging 3. Она сама делает этот COUNT под капотом, когда это необходимо, и упрощает работу с RecyclerView.
Хотите, чтобы я показал, как создать индекс на выражение (Functional Index) через миграцию, чтобы группировка по (date + 10800000) / timeFrame работала мгновенно?




     */

}