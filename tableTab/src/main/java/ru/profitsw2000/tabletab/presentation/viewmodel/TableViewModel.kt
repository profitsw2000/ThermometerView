package ru.profitsw2000.tabletab.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.model.state.filterscreen.LetterCodesLoadState
import ru.profitsw2000.data.model.state.filterscreen.LocalIdsLoadState
import ru.profitsw2000.data.model.state.filterscreen.SensorIdsLoadState
import ru.profitsw2000.data.room.database.AppDatabase

class TableViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor
) : ViewModel() {

    private var sensorHistoryDataEntitySize = 0
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var _sensorIdsLoadLiveData: MutableLiveData<SensorIdsLoadState>
    val sensorIdsLoadLiveData: LiveData<SensorIdsLoadState> by this::_sensorIdsLoadLiveData

    private lateinit var _localIdsLoadLiveData: MutableLiveData<LocalIdsLoadState>
    val localIdsLoadLiveData: LiveData<LocalIdsLoadState> by this::_localIdsLoadLiveData

    private lateinit var _letterCodesLoadLiveData: MutableLiveData<LetterCodesLoadState>
    val letterCodesLoadLiveData: LiveData<LetterCodesLoadState> by this::_letterCodesLoadLiveData

    val historyListPagedData =
        sensorHistoryInteractor
            .getHistoryPagedData(false)
            .cachedIn(viewModelScope)
            .asLiveData()

    init {
        viewModelScope.launch {
            sensorHistoryDataEntitySize = getHistoryDataEntitySize()
        }
    }

    fun checkDatabaseUpdate(lifecycleScope: CoroutineScope) {
        lifecycleScope.launch {
            val tableSize = getHistoryDataEntitySize()
            if (tableSize != sensorHistoryDataEntitySize) {
                //sensorHistoryInteractor.updateHistoryPagedData(false)
                sensorHistoryDataEntitySize = tableSize
            }
        }
    }

    private suspend fun getHistoryDataEntitySize(): Int {
        val deferred: Deferred<Int> = coroutineScope.async {
            try {
                sensorHistoryInteractor.getHistoryDataEntitySize(false)
            } catch (e: Exception) {
                e.printStackTrace()
                sensorHistoryDataEntitySize
            }
        }
        return deferred.await()
    }

}