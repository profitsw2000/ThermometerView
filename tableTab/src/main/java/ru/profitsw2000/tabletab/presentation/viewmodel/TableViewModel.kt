package ru.profitsw2000.tabletab.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import ru.profitsw2000.data.data.local.source.HistoryListPagingSource
import ru.profitsw2000.data.interactor.SensorHistoryInteractor
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.database.AppDatabase

class TableViewModel(
    private val sensorHistoryInteractor: SensorHistoryInteractor
) : ViewModel() {

    val historyListPagedData =
        sensorHistoryInteractor
            .getHistoryPagedData(false)
            .cachedIn(viewModelScope)
            .asLiveData()

}