package ru.profitsw2000.data.data.local.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.profitsw2000.data.domain.filter.SensorHistoryTableFilterRepository
import ru.profitsw2000.data.mappers.SensorHistoryMapper
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.data.room.database.AppDatabase

class HistoryListPagingSource(
    private val database: AppDatabase,
    private val sensorHistoryMapper: SensorHistoryMapper,
    private val sensorHistoryTableFilterRepository: SensorHistoryTableFilterRepository
) : PagingSource<Int, SensorHistoryDataModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SensorHistoryDataModel> {
        val page = params.key ?: 0

        return try {
            val sensorHistoryListPage = database.sensorHistoryDao.getSensorHistoryList(params.loadSize, page*params.loadSize)

            LoadResult.Page(
                data = sensorHistoryMapper.map(sensorHistoryListPage),
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (sensorHistoryListPage.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SensorHistoryDataModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}