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
import ru.profitsw2000.data.domain.filter.SensorHistoryGraphFilterRepository
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
    val letterCodesMutableList = mutableListOf<Int>()
    var offset = 0

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
            sensorHistoryGraphFilterRepository.sensorIdList = sensorIdsList ?: listOf<Long>()
            loadInitData()
        }
    }

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.lifecycleScope = coroutineScope
    }

    private suspend fun loadInitData() {
        val graphData = getFilteredSensorsHistoryLists()
        if (graphData != null)
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Success(graphData)
        else
            _sensorHistoryListLiveData.value = SensorHistoryDataLoadState.Error("Database error.")
    }

    fun loadData(newItemsNumber: Int) {
        offset = if (offset + newItemsNumber < 0) 0
        else offset + newItemsNumber

        lifecycleScope.launch {
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
            val beginDate = firstSensorHistoryList.first().date
            val endDate = firstSensorHistoryList.last().date
            val subsequentSensorsHistoryLists = getSubsequentHistoryLists(beginDate, endDate)

            if (subsequentSensorsHistoryLists.isNullOrEmpty()) null
            else listOf(firstSensorHistoryList) + subsequentSensorsHistoryLists
        } else null

        return result
    }

    private suspend fun getFirstSensorHistoryList(): List<SensorHistoryDataModel>? = withContext(Dispatchers.IO) {
        val deferred: Deferred<List<SensorHistoryDataModel>?> = ioCoroutineScope.async {
            try {
                val sensorHistoryDataList = sensorHistoryInteractor.getGraphFirstCurveSensorHistoryList(
                    filter = sensorHistoryGraphFilterRepository,
                    limit = SENSOR_HISTORY_DATA_LOAD_SIZE,
                    offset = offset,
                    false
                )
                sensorHistoryMapper.map(sensorHistoryDataList)
            } catch (exception: Exception) {
                null
            }
        }
        return@withContext deferred.await()
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
                            filter = sensorHistoryGraphFilterRepository,
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

}