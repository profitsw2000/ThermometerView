package ru.profitsw2000.data.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.profitsw2000.core.utils.constants.DATA_EXCHANGE_INTERVAL
import ru.profitsw2000.core.utils.constants.DATE_TIME_DATA_INTERVAL_MS
import ru.profitsw2000.data.domain.DateTimeRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DateTimeRepositoryImpl : DateTimeRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val mutableDateTimeDataString: MutableStateFlow<String> = MutableStateFlow(getCurrentDateTimeString())
    override val dateTimeDataString: StateFlow<String>
        get() = mutableDateTimeDataString

    private val mutableDataExchangeStartSignal: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val dataExchangeStartSignal: StateFlow<Boolean>
        get() = mutableDataExchangeStartSignal

    private var intervalsCounter = 0

    init {
        startDateTimeFlow()
    }

    override fun getCurrentDateTimeArray(): Array<Int> {
        val calendar = Calendar.getInstance()
        val dayOfWeek =
            if (calendar.get(Calendar.DAY_OF_WEEK) - 1 < 1) 7
            else calendar.get(Calendar.DAY_OF_WEEK) - 1

        return arrayOf(
            calendar.get(Calendar.SECOND),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.HOUR_OF_DAY),
            dayOfWeek,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.YEAR)%100
        )
    }

    private fun startDateTimeFlow() {
        coroutineScope.launch {
            while (isActive) {
                intervalsCounter = (intervalsCounter++) % DATA_EXCHANGE_INTERVAL
                mutableDataExchangeStartSignal.value = (intervalsCounter == 0)
                mutableDateTimeDataString.value = getCurrentDateTimeString()
                delay(DATE_TIME_DATA_INTERVAL_MS)
            }
        }
    }

    private fun getCurrentDateTimeString(): String {
        val formatter = SimpleDateFormat("HH:mm:ss dd.MM.yy")
        val date = Date()
        return formatter.format(date)
    }
}