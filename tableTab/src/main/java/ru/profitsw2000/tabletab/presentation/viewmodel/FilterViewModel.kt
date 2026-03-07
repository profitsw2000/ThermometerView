package ru.profitsw2000.tabletab.presentation.viewmodel

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
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.core.utils.constants.getCodeFromLetterList
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.enumer.TimeFrameDataObtainingMethod
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.model.state.filterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.filterscreen.LocalIdsLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

class FilterViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor,
    private val sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
) : ViewModel() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _sensorIdsLoadLiveData: MutableLiveData<SensorIdsLoadState> = MutableLiveData<SensorIdsLoadState>()
    val sensorIdsLoadLiveData: LiveData<SensorIdsLoadState> by this::_sensorIdsLoadLiveData

    private val _localIdsLoadLiveData: MutableLiveData<LocalIdsLoadState> = MutableLiveData<LocalIdsLoadState>()
    val localIdsLoadLiveData: LiveData<LocalIdsLoadState> by this::_localIdsLoadLiveData

    private val _letterCodesLoadLiveData: MutableLiveData<LetterCodesLoadState> = MutableLiveData<LetterCodesLoadState>()
    val letterCodesLoadLiveData: LiveData<LetterCodesLoadState> by this::_letterCodesLoadLiveData

    private val _dateRangeStringLiveData: MutableLiveData<String> =
        MutableLiveData(getStringDateRange(
                sensorHistoryTableFilterRepository.fromDate,
                sensorHistoryTableFilterRepository.toDate
            )
        )
    val dateRangeStringLiveData: LiveData<String> by this::_dateRangeStringLiveData

    val checkedSensorNumberList = mutableListOf<Long>()
    val checkedLocalIdList = mutableListOf<Int>()
    val checkedLetterList = mutableListOf<String>()

    fun loadFilterElements(lifecycleScope: CoroutineScope) {
        _sensorIdsLoadLiveData.value = SensorIdsLoadState.Loading
        _localIdsLoadLiveData.value = LocalIdsLoadState.Loading
        _letterCodesLoadLiveData.value = LetterCodesLoadState.Loading
        lifecycleScope.launch {
            _sensorIdsLoadLiveData.value = querySensorIdList()
            _localIdsLoadLiveData.value = queryLocalIdList()
            _letterCodesLoadLiveData.value = queryLetterCodesList()
        }
    }

    private suspend fun querySensorIdList(): SensorIdsLoadState {
        val deferred: Deferred<SensorIdsLoadState> = coroutineScope.async {
            try {
                val sensorIdsList = sensorHistoryInteractor.getAllSensorIds(false)
                SensorIdsLoadState.Success(sensorIdsList)
            } catch (e: Exception) {
                e.printStackTrace()
                SensorIdsLoadState.Error
            }
        }
        return deferred.await()
    }

    private suspend fun queryLocalIdList(): LocalIdsLoadState {
        val deferred: Deferred<LocalIdsLoadState> = coroutineScope.async {
            try {
                val localIdsList = sensorHistoryInteractor.getAllSensorLocalIds(false)
                LocalIdsLoadState.Success(localIdsList)
            } catch (e: Exception) {
                e.printStackTrace()
                LocalIdsLoadState.Error
            }
        }
        return deferred.await()
    }

    private suspend fun queryLetterCodesList(): LetterCodesLoadState {
        val deferred: Deferred<LetterCodesLoadState> = coroutineScope.async {
            try {
                val letterCodesList = sensorHistoryInteractor.getAllLetterCodes(false)
                LetterCodesLoadState.Success(letterCodesList)
            } catch (e: Exception) {
                e.printStackTrace()
                LetterCodesLoadState.Error
            }
        }
        return deferred.await()
    }

    fun <T> addElementToCheckedList(element: T) {
        when(element) {
            is Int -> checkedLocalIdList.add(element)
            is Long -> checkedSensorNumberList.add(element)
            is String -> checkedLetterList.add(element)
            else -> {}
        }
    }

    fun <T> removeElementFromCheckedList(element: T) {
        when(element) {
            is Int -> checkedLocalIdList.remove(element)
            is Long -> checkedSensorNumberList.remove(element)
            is String -> checkedLetterList.remove(element)
            else -> {}
        }
    }

    fun isHistoryListOrderAscending(): Boolean {
        return sensorHistoryTableFilterRepository.isAscendingOrder
    }

    fun setHistoryListOrder(isAscending: Boolean) {
        sensorHistoryTableFilterRepository.isAscendingOrder = isAscending
        sensorHistoryInteractor.invalidateDataSource(false)
    }

    fun applyFilters(timeFrameMillis: Long, timeFrameDataObtainingMethod: TimeFrameDataObtainingMethod) {
        sensorHistoryTableFilterRepository.apply {
            sensorIdList = checkedSensorNumberList
            localIdList = checkedLocalIdList
            letterCodeList = getCodeFromLetterList(checkedLetterList)
            this.timeFrameMillis = timeFrameMillis
            this.timeFrameDataObtainingMethod = timeFrameDataObtainingMethod
        }
        sensorHistoryInteractor.invalidateDataSource(false)
    }

    fun setDateRangeLiveData() {
        _dateRangeStringLiveData.value = getStringDateRange(
            sensorHistoryTableFilterRepository.fromDate,
            sensorHistoryTableFilterRepository.toDate
        )
    }

    fun setFilterDateRange(fromDate: Date?, toDate: Date?) {
        sensorHistoryTableFilterRepository.fromDate = fromDate
        sensorHistoryTableFilterRepository.toDate = toDate
        sensorHistoryInteractor.invalidateDataSource(false)
        setDateRangeLiveData()
    }

    private fun getStringDateRange(fromDate: Date?, toDate: Date?): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")

        return if (fromDate == null || toDate == null) "За всё время"
            else "${simpleDateFormat.format(fromDate)} - ${simpleDateFormat.format(toDate)}"
    }

    fun getTimeFramePeriod(): Long {
        return sensorHistoryTableFilterRepository.timeFrameMillis
    }

    fun getTimeFrameDataObtainingMethod(): TimeFrameDataObtainingMethod {
        return sensorHistoryTableFilterRepository.timeFrameDataObtainingMethod
    }
}